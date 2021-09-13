package tororo1066.coinflip.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.coinflip.CoinFlip
import tororo1066.coinflip.CoinFlip.Companion.plugin

class SwitchMode : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args[1].toBoolean()){
            if (plugin.config.getBoolean("mode")){
                CoinFlip.sendMsg(sender,"§4すでにonになっています！")
                return true
            }

            plugin.config.set("mode",true)
            plugin.saveConfig()
            CoinFlip.pluginEnable = true
            CoinFlip.sendMsg(sender,"§bmodeをtrueにしました")
            return true
        }else{
            if (!plugin.config.getBoolean("mode")){
                CoinFlip.sendMsg(sender,"§4すでにoffになっています！")
                return true
            }

            plugin.config.set("mode",false)
            plugin.saveConfig()
            CoinFlip.pluginEnable = false
            CoinFlip.sendMsg(sender,"§bmodeをfalseにしました")
            return true
        }
    }
}