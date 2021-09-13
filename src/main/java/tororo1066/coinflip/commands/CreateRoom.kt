package tororo1066.coinflip.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.coinflip.CoinFlip
import tororo1066.coinflip.CoinFlipRoom
import java.util.regex.Pattern
import kotlin.math.round

class CreateRoom : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!CoinFlip.pluginEnable){
            CoinFlip.sendMsg(sender,"§4CoinFlipは停止中です")
            return true
        }

        if (sender !is Player){
            CoinFlip.sendMsg(sender,"§4このコマンドはプレイヤーのみ実行できます")
            return true
        }

        val minmoney = CoinFlip.coinConfig.getDouble("minmoney")
        val maxmoney = CoinFlip.coinConfig.getDouble("maxmoney")






        val bet = round(args[1].toDouble())
        if (bet !in minmoney..maxmoney){
            CoinFlip.sendMsg(sender,"§4金額は$minmoney~${maxmoney}にしてください")
            return true
        }
        if (CoinFlip.vault.getBalance(sender.uniqueId) < bet){
            CoinFlip.sendMsg(sender,"§4お金が足りません")
            return true
        }

        if (CoinFlip.coinFlipData.containsKey(sender.uniqueId)){
            CoinFlip.sendMsg(sender,"§4あなたはゲームに参加中です")
            return true
        }

        CoinFlip.coinFlipData[sender.uniqueId] = CoinFlipRoom(sender.uniqueId, bet ,args[2] == "heads")//部屋データを入れる
        CoinFlip.coinFlipData[sender.uniqueId]?.data?.put(sender.uniqueId, CoinFlipRoom.PlayerData(sender.uniqueId,args[2] == "heads"))//プレイヤーデータを入れる
        CoinFlip.coinFlipData[sender.uniqueId]?.start()//スレッドをスタートする(ゲーム)





        return true

    }

}