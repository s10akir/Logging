package work.mojamoja.logging

import org.bukkit.plugin.java.JavaPlugin

class Logging: JavaPlugin() {
    override fun onEnable() {
        logger.info("Logging enabled.")
        server.pluginManager.registerEvents(Cutter(), this)

        // config.ymlが存在しない場合、デフォルトのconfig.ymlをplugins/Logging以下に作成する。
        this.saveDefaultConfig()
    }

    override fun onDisable() {
        logger.info("Logging disabled.")
    }
}