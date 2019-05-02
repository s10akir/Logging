package work.mojamoja.logging

import org.bukkit.plugin.java.JavaPlugin

class Logging: JavaPlugin() {
    override fun onEnable() {
        logger.info("Logging enabled.")
        server.pluginManager.registerEvents(Cutter(), this)
    }

    override fun onDisable() {
        logger.info("Logging disabled.")
    }
}