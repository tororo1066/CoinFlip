package tororo1066.coinflip.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.coinflip.CoinFlip
import tororo1066.coinflip.CoinFlipLog

class ShowLogOp : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val logdata = CoinFlipLog().showlog(Bukkit.getOfflinePlayer(args[1]).uniqueId)

        if (!logdata.succes){
            CoinFlip.sendMsg(sender,"§4データが存在しません")
            return true
        }

        sender.sendMessage("§e========${logdata.mcid}の戦績========")
        sender.sendMessage("§c勝利数：${logdata.win}回")
        sender.sendMessage("§b敗北数：${logdata.lose}回")
        sender.sendMessage("§d勝率：${logdata.winingpersentage}%(切り捨て)")
        sender.sendMessage("${if (logdata.wincollect-logdata.losecollect < 0) "§c" else "§b"}総獲得賞金：${CoinFlip.format(logdata.wincollect-logdata.losecollect)}円")
        sender.sendMessage("§a最大獲得賞金：${CoinFlip.format(logdata.maxcollect)}円")
        sender.sendMessage("§e========${logdata.mcid}の戦績========")

        return true
    }
}