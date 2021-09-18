package work.mojamoja.logging

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.Vector
import kotlin.random.Random

/**
 * プレイヤーがメインハンドに持っているツールでの破壊を再現する。
 *
 * @param player ブロック破壊を実行するPlayerオブジェクト
 * @param block 破壊対象のBlockオブジェクト
 */
fun breakWithMainHand(player: Player, block: Block) {
    val tool = player.inventory.itemInMainHand

    block.breakNaturally(tool)

    // 耐久値の概念が存在するツールだった場合にダメージを加算する
    val damageable = tool.itemMeta as? Damageable
    if (damageable != null) {
        // Unbreakingが付いていれば確率でダメージを加算する
        val durabilityLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY)
        if (Random.nextInt(durabilityLevel + 1) == 0) {
            damageable.damage += 1
        }

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

/**
 * 与えられたブロックと同じブロックで構成される塊を再帰的に探索し、引数で与えられたArrayListオブジェクトへ追加する。
 *
 * @param block 起点となるBlockオブジェクト
 * @param lump Blockオブジェクトを格納するArrayListオブジェクト
 */
fun parseLump(block: Block, lump: ArrayList<Block>) {
    // すでに探索済みのブロックであった場合何もせずにリターン
    if (lump.indexOf(block) != -1) return

    lump.add(block)

    for (x in -1..1) {
        for (y in 0..1) {
            for (z in -1..1) {
                val nextBlock = block.location.add(Vector(x, y, z)).block

                if (isLog(nextBlock)) {
                    parseLump(nextBlock, lump)
                }
            }
        }
    }
}

/**
 * 与えられたブロックが原木であるかを判定する。
 * ただし、「皮を剥いだ原木」はfalseを返す
 *
 * @param block 判定するBlockオブジェクト
 * @return 原木か否か
 */
fun isLog(block: Block): Boolean {
    return  block.type == Material.OAK_LOG ||
            block.type == Material.ACACIA_LOG ||
            block.type == Material.BIRCH_LOG ||
            block.type == Material.DARK_OAK_LOG ||
            block.type == Material.JUNGLE_LOG ||
            block.type == Material.SPRUCE_LOG
}

/**
 * 与えられたアイテムの種類での一括破壊を行っていいか、config.ymlを基に判定する。
 *
 * @param tool 判定するアイテム
 * @param config FileConfiguration
 * @return 一括破壊の可否
 */
fun isEligible(tool: ItemStack, config: FileConfiguration): Boolean {
    return isAxe(tool) && isActive(tool, config)
}

/**
 * 与えられたアイテムが斧であるかを判定する。
 *
 * @param tool 判定するItemStackオブジェクト
 * @return 斧か否か
 */
fun isAxe(tool: ItemStack): Boolean {
    return  tool.type == Material.WOODEN_AXE ||
            tool.type == Material.STONE_AXE ||
            tool.type == Material.IRON_AXE ||
            tool.type == Material.DIAMOND_AXE ||
            tool.type == Material.NETHERITE_AXE
}

/**
 * 与えられた斧での一括破壊が有効化されているか、config.ymlを基に判定する。
 *
 * @param tool 判定する斧
 * @param config FileConfiguration
 * @return 有効化されているか否か
 */
fun isActive(tool: ItemStack, config: FileConfiguration): Boolean {
    when (tool.type) {
        Material.WOODEN_AXE -> {
            return config.getBoolean("wooden")
        }
        Material.STONE_AXE -> {
            return config.getBoolean("stone")
        }
        Material.IRON_AXE -> {
            return config.getBoolean("iron")
        }
        Material.DIAMOND_AXE -> {
            return config.getBoolean("diamond")
        }
        Material.NETHERITE_AXE -> {
            return config.getBoolean("netherite")
        }
        else -> {
            return false
        }
    }
}