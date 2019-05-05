package work.mojamoja.logging

import org.bukkit.plugin.java.JavaPlugin

class Logging: JavaPlugin() {
    override fun onEnable() {
        logger.info("Logging enabled.")
        server.pluginManager.registerEvents(Cutter(), this)

        // config.yml を読み込む
        this.saveDefaultConfig()
    }

    override fun onDisable() {
        logger.info("Logging disabled.")
    }
}