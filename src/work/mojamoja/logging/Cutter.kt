package work.mojamoja.logging

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.meta.Damageable

class Cutter: Listener {
    @EventHandler fun listen(e: BlockBreakEvent) {
        val logging = Bukkit.getPluginManager().getPlugin("Logging") as Logging
        val config = logging.config

        val block = e.block
        val player =  e.player
        val tool = player.inventory.itemInMainHand

        // 対象のブロック、ツールでなかった場合 もしくはスニーク中の場合はアーリーリターン
        if (!(isLog(block) && isEligible(tool, config)) || player.isSneaking) return


        // この時点で斧であることが保証されているのでアンセーフキャスト
        val damageable = tool.itemMeta as Damageable
        val durability = tool.type.maxDurability - damageable.damage
        val tree: ArrayList<Block> = ArrayList()

        parseLump(block, tree)

        // 斧の耐久値が木を切り倒すのに足りなかった場合はアーリーリターン
        if (durability < tree.size) return


        e.isCancelled = true

        // 木こり
        for (log in tree) {
            breakWithMainHand(player, log)
        }
    }
}
