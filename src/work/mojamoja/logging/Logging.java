package work.mojamoja.logging;

import org.bukkit.plugin.java.JavaPlugin;

public class Logging extends JavaPlugin {
    @Override
    public void onEnable() {
        System.out.println("Logging enabled.");
        getServer().getPluginManager().registerEvents(new Cutter(), this);
    }

    @Override
    public void onDisable() {
        System.out.println("Logging disabled.");
    }
}
