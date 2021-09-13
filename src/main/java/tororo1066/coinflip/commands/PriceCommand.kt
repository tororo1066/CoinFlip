package tororo1066.coinflip.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.coinflip.CoinFlip

class PriceCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        CoinFlip.coinConfig.set("minmoney",args[1].toDouble())
        CoinFlip.coinConfig.set("maxmoney",args[2].toDouble())

        CoinFlip.sendMsg(sender,"§b金額の指定を${args[1]}~${args[2]}に設定しました")

        return true
    }
}