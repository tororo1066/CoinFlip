package tororo1066.coinflip.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.coinflip.CoinFlip
import tororo1066.coinflip.CoinFlipLog
import java.util.concurrent.Callable

class ShowLog : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!CoinFlip.pluginEnable){
            CoinFlip.sendMsg(sender,"§4CoinFlipは停止中です")
            return true
        }

        if (sender !is Player){
            CoinFlip.sendMsg(sender,"§4このコマンドはプレイヤーのみ実行できます")
            return true
        }
        CoinFlip.es.execute {
            val logdata = CoinFlipLog().showlog(sender.uniqueId)
            if (!logdata.success){
                CoinFlip.sendMsg(sender,"§4データが存在しません")
                return@execute
            }
            sender.sendMessage("§e========${logdata.mcid}の戦績========")
            sender.sendMessage("§c勝利数：${logdata.win}回")
            sender.sendMessage("§b敗北数：${logdata.lose}回")
            sender.sendMessage("§d勝率：${logdata.winingpersentage}%(切り捨て)")
            sender.sendMessage("${if (logdata.wincollect-logdata.losecollect < 0) "§c" else "§b"}総獲得賞金：${CoinFlip.format(logdata.wincollect-logdata.losecollect)}円")
            sender.sendMessage("§a最大獲得賞金：${CoinFlip.format(logdata.maxcollect)}円")
            sender.sendMessage("§e========${logdata.mcid}の戦績========")
            return@execute
        }


        return true
    }
}