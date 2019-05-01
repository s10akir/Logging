package work.mojamoja.logging;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Cutter implements Listener {
    @EventHandler
    public void listen(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // 対象のブロック、ツールでなかった場合 もしくはスニーク中の場合はアーリーリターン
        if (!(isLog(block) && isAxe(tool)) || player.isSneaking()) {
            return;
        }

        e.setCancelled(true);
        cutting(player, block);

        // 起点のブロックから周囲17ブロック(起点を中心とした3x3の立方体から起点以下のブロックを除外)を探索し、対象のブロックであった場合にBlockBreakEventを呼び出す
        // 隣接していないブロッック（斜め位置）も破壊されるが。アカシアの木などの木こりに必要なので仕様とする
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block nextBlock = block.getLocation().add(new Vector(x, y, z)).getBlock();

                    if (isLog(nextBlock)) {
                        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(nextBlock, player);
                        Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
                    }
                }
            }
        }
    }

    // プレイヤーがメインハンドのツールでブロックを破壊したことを再現するメソッド
    private void cutting(Player player, Block block) {
        ItemStack tool = player.getInventory().getItemInMainHand();

        block.breakNaturally(tool);
        tool.setDurability((short) (tool.getDurability() + 1));

        // 武器の耐久値が0になったとき
        if (tool.getType().getMaxDurability() == tool.getDurability()) {
            // 武器の破壊を再現
            tool.setAmount(0);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
    }

    private boolean isLog(Block block) {
        return
                block.getType() == Material.OAK_LOG ||
                block.getType() == Material.ACACIA_LOG ||
                block.getType() == Material.BIRCH_LOG ||
                block.getType() == Material.DARK_OAK_LOG ||
                block.getType() == Material.JUNGLE_LOG ||
                block.getType() == Material.SPRUCE_LOG;
    }

    private boolean isAxe(ItemStack tool) {
        return
                tool.getType() == Material.WOODEN_AXE ||
                tool.getType() == Material.STONE_AXE ||
                tool.getType() == Material.IRON_AXE ||
                tool.getType() == Material.DIAMOND_AXE;
    }
}
