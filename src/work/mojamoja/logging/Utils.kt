package work.mojamoja.logging

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.Vector

fun breakWithMainHand(player: Player, block: Block) {
    val tool = player.inventory.itemInMainHand

    block.breakNaturally(tool)

    // 耐久値の概念が存在するツールだった場合にダメージを加算する
    val damageable = tool.itemMeta as? Damageable
    if (damageable != null) {
        damageable.damage += 1

        // 武器の耐久値が0になったとき
        if (damageable.damage == tool.type.maxDurability.toInt()) {
            // 武器の破壊を再現
            tool.amount = 0
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
        } else {
            val itemMeta = damageable as ItemMeta
            tool.itemMeta = itemMeta
        }
    }
}

fun treeParser(block: Block, tree: ArrayList<Block>) {
    // すでに探索済みのブロックであった場合何もせずにリターン
    if (tree.indexOf(block) != -1) return

    tree.add(block)

    for (x in -1..1) {
        for (y in 0..1) {
            for (z in -1..1) {
                val nextBlock = block.location.add(Vector(x, y, z)).block

                if (isLog(nextBlock)) {
                    treeParser(nextBlock, tree)
                }
            }
        }
    }
}

fun isLog(block: Block): Boolean {
    return  block.type == Material.OAK_LOG ||
            block.type == Material.ACACIA_LOG ||
            block.type == Material.BIRCH_LOG ||
            block.type == Material.DARK_OAK_LOG ||
            block.type == Material.JUNGLE_LOG ||
            block.type == Material.SPRUCE_LOG
}

fun isAxe(tool: ItemStack): Boolean {
    return  tool.type == Material.WOODEN_AXE ||
            tool.type == Material.STONE_AXE ||
            tool.type == Material.IRON_AXE ||
            tool.type == Material.DIAMOND_AXE
}
