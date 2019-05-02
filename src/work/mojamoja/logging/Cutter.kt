package work.mojamoja.logging

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class Cutter: Listener {
    @EventHandler fun listen(e: BlockBreakEvent) {
        val block = e.block
        val player =  e.player
        val tool = player.inventory.itemInMainHand

        // 対象のブロック、ツールでなかった場合 もしくはスニーク中の場合はアーリーリターン
        if (!(isLog(block) && isAxe(tool)) || player.isSneaking) {
            return
        }

        e.isCancelled = true
        cutting(player, block)

        // 起点のブロックから周囲17ブロック(起点を中心とした3x3の立方体から起点以下のブロックを除外)を探索し、対象のブロックであった場合にBlockBreakEventを呼び出す
        // 隣接していないブロック（斜め位置）も破壊されるが。アカシアの木などの木こりに必要なので仕様とする
        for (x in -1..1) {
            for (y in 0..1) {
                for (z in -1..1) {
                    val nextBlock = block.location.add(Vector(x, y, z)).block

                    if (isLog(nextBlock)) {
                        val blockBreakEvent = BlockBreakEvent(nextBlock, player)
                        Bukkit.getServer().pluginManager.callEvent(blockBreakEvent)
                    }
                }
            }
        }
    }

    // プレイヤーがメインハンドのツールでブロックを破壊したことを再現するメソッド
    private fun cutting(player: Player, block: Block) {
        val tool = player.inventory.itemInMainHand

        block.breakNaturally(tool)
        tool.durability = (tool.durability + 1).toShort()

        // 武器の耐久値が0になったとき
        if (tool.durability == tool.type.maxDurability) {
            // 武器の破壊を再現
            tool.amount = 0
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
        }
    }

    private fun isLog(block: Block): Boolean {
        return block.type == Material.OAK_LOG ||
               block.type == Material.ACACIA_LOG ||
               block.type == Material.BIRCH_LOG ||
               block.type == Material.DARK_OAK_LOG ||
               block.type == Material.JUNGLE_LOG ||
               block.type == Material.SPRUCE_LOG
    }

    private fun isAxe(tool: ItemStack): Boolean {
        return tool.type == Material.WOODEN_AXE ||
               tool.type == Material.STONE_AXE ||
               tool.type == Material.IRON_AXE ||
               tool.type == Material.DIAMOND_AXE
    }
}
