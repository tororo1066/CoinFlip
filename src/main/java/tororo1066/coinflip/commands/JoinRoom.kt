package tororo1066.coinflip.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.coinflip.CoinFlip

class JoinRoom : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!CoinFlip.pluginEnable){
            CoinFlip.sendMsg(sender,"§4CoinFlipは停止中です")
            return true
        }

        if (sender !is Player){
            CoinFlip.sendMsg(sender,"§4このコマンドはプレイヤーのみ実行できます")
            return true
        }

        val uuid = Bukkit.getOfflinePlayer(args[1]).uniqueId
        if (!CoinFlip.coinFlipData.containsKey(uuid)){
            CoinFlip.sendMsg(sender,"§4ゲームが存在しません")
            return true
        }

        if (CoinFlip.coinFlipData[uuid]!!.data.size == 2){
            CoinFlip.sendMsg(sender,"§4すでに部屋が埋まっています")
            return true
        }

        if (CoinFlip.coinFlipData.containsKey(sender.uniqueId)){
            CoinFlip.sendMsg(sender,"§4あなたは部屋を作成しています")
            return true
        }

        if (CoinFlip.vault.getBalance(sender.uniqueId) < CoinFlip.coinFlipData[uuid]!!.bet){
            CoinFlip.sendMsg(sender,"§4所持金が足りません")
            return true
        }

        CoinFlip.vault.withdraw(sender.uniqueId,CoinFlip.coinFlipData[uuid]!!.bet)
        CoinFlip.coinFlipData[uuid]!!.addJoinPlayer(sender)

        return true
    }
}