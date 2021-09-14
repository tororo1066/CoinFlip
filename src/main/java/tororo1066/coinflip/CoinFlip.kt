package tororo1066.coinflip

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.coinflip.Utils.MySQL.MySQLAPI
import tororo1066.coinflip.Utils.SInventory.SInventoryItem
import tororo1066.coinflip.Utils.SItemStack
import tororo1066.coinflip.Utils.VaultAPI
import tororo1066.coinflip.commands.CoinFlipCommand
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.HashMap

class CoinFlip : JavaPlugin(){



    companion object{
        var pluginEnable = true
        lateinit var coinConfig : FileConfiguration
        lateinit var vault : VaultAPI
        lateinit var mysql : MySQLAPI
        lateinit var plugin : CoinFlip
        lateinit var es : ExecutorService
        val coinFlipData = HashMap<UUID,CoinFlipRoom>()
        lateinit var heads: SInventoryItem
        lateinit var tails: SInventoryItem

        fun sendMsg(p : CommandSender, msg : String){
            p.sendMessage("§f[§e§lCoinFlip§f]§r$msg")
        }

        fun sendMsg(p : Player, msg : String){
            p.sendMessage("§f[§e§lCoinFlip§f]§r$msg")
        }

        fun broadcast(s : String){
            Bukkit.broadcast(Component.text("§f[§e§lCoinFlip§f]§r$s"), Server.BROADCAST_CHANNEL_USERS)
        }

        fun format(double: Double):String{
            return String.format("%,.0f",double)
        }
    }

    override fun onEnable() {
        saveDefaultConfig()
        vault = VaultAPI()
        mysql = MySQLAPI(this)
        coinConfig = config
        plugin = this
        es = Executors.newCachedThreadPool()
        pluginEnable = config.getBoolean("mode")
        heads = SInventoryItem(
            SItemStack(plugin.config.getString("cointex.heads.material")?.let { Material.valueOf(it) }).setDisplayName("§e表").setCustomModelData(
                plugin.config.getInt("cointex.heads.csm")).build()).clickable(false)
        tails = SInventoryItem(SItemStack(plugin.config.getString("cointex.tails.material")?.let { Material.valueOf(it) }).setDisplayName("§b裏").setCustomModelData(
            plugin.config.getInt("cointex.tails.csm")).build()).clickable(false)

        getCommand("coinflip")?.setExecutor(CoinFlipCommand())
        getCommand("coinflip")?.tabCompleter = CoinFlipCommand()

    }

    override fun onDisable() {
        es.shutdownNow()
    }
}