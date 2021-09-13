package tororo1066.coinflip.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.coinflip.CoinFlip

class PriceCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {


        var min = args[1].toDouble()
        var max = args[2].toDouble()
        if (min <= 0.0){
            min = 0.0
            CoinFlip.coinConfig.set("minmoney",0.0)
        }else{
            CoinFlip.coinConfig.set("minmoney",args[1].toDouble())
        }

        if (min > max){
            max = min
            CoinFlip.coinConfig.set("maxmoney", min)
        }else{
            CoinFlip.coinConfig.set("maxmoney",args[2].toDouble())
        }
        CoinFlip.plugin.saveConfig()

        CoinFlip.sendMsg(sender,"§b金額の指定を${min}~${max}に設定しました")

        return true
    }
}