package work.mojamoja.logging

import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

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
