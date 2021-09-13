package tororo1066.coinflip

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.checkerframework.checker.nullness.qual.NonNull
import org.jetbrains.annotations.Nullable
import tororo1066.coinflip.CoinFlip.Companion.broadcast
import tororo1066.coinflip.CoinFlip.Companion.mysql
import tororo1066.coinflip.CoinFlip.Companion.plugin
import tororo1066.coinflip.CoinFlip.Companion.vault
import tororo1066.coinflip.Utils.SInventory.SInventory
import tororo1066.coinflip.Utils.SInventory.SInventoryItem
import tororo1066.coinflip.Utils.SItemStack
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.random.Random
import kotlin.random.nextInt

class CoinFlipRoom(private val playeruuid : UUID, val bet : Double, private val heads: Boolean) : Thread() {


    /**
     * PlayerDataを保存するHashMap。
     * LinkedHashMapで追加された順に並ぶようになっている。
     */
    val data = LinkedHashMap<UUID,PlayerData>()


    /**
     * プレイヤーのデータを保存するクラス
     * @param uuid そのプレイヤーのuuid
     * @param heads 表だった場合true、裏だった場合false
     */
    class PlayerData(uuid: UUID, heads: Boolean) {
        var name : String = Bukkit.getPlayer(uuid)!!.name
        var gui : SInventory = SetUpGUI(uuid,heads)

    }

    /**
     * 参加側の処理
     * @param p 参加するプレイヤー
     */
    fun addJoinPlayer(p : Player){
        val playerdata = PlayerData(p.uniqueId,!heads)
        data[p.uniqueId] = playerdata
    }

    /**
     * uuidからオンラインのプレイヤーを獲得する処理
     * @param uuid
     * @return Player(オフラインだった場合null)
     */
    private fun player(uuid : UUID): @Nullable Player? {
        return Bukkit.getPlayer(uuid)
    }

    private fun runTask(Unit: ()->Unit){
        Bukkit.getScheduler().runTask(plugin, Runnable {
            Unit.invoke()
            return@Runnable
        })
    }

    /**
     * ゲーム中のプレイヤーにメッセージを送る処理
     * @param s 送る文字列
     */
    private fun allPlayerSend(s : String){
        for (p in data){
            player(p.key)?.sendMessage(s)
        }
    }

    /**
     * ゲーム中のプレイヤーに音を送る処理
     * @param sound 音
     * @param volume 音の大きさ
     * @param pitch ピッチ
     */
    private fun allPlaySound(sound : Sound, volume : Float, pitch : Float){
        for (p in data){
            player(p.key)?.location?.let { player(p.key)?.playSound(it, sound, volume, pitch) }
        }
    }

    /**
     * ホバーテキストとクリックコマンドを複合させたComponentを返す処理
     * @param s メッセージ
     * @param cmd コマンド(/も含んでいれる)
     * @param hover ホバーテキスト
     * @return Component
     */
    private fun runcmd(s : String, cmd : String, hover : String): @NonNull Component {
        return text(s).clickEvent(ClickEvent.runCommand(cmd)).hoverEvent(HoverEvent.showText(text(hover))).asComponent()
    }

    /**
     * DBにプレイヤーのログを書き込む処理
     * @param uuid そのプレイヤーのuuid
     * @param win 勝利したかしてないか
     * @return 処理に失敗したらfalseが返る
     */
    private fun savePlayerLog(uuid : UUID, win : Boolean): Boolean {
        val rs = mysql.query("select * from coinflip_player_log where uuid = '$uuid'")
        if (rs != null && rs.next()){
            if (!mysql.execute("update coinflip_player_log set " +
                        "mcid = '${Bukkit.getOfflinePlayer(uuid).name}', win = win+${if (win) 1 else 0}, lose = lose+${if (win) 0 else 1}," +
                        "wincollect = wincollect+${if (win) bet else 0}, losecollect = losecollect+${if (win) 0 else bet}, maxcollect = ${if (rs.getDouble("maxcollect") < bet) bet else rs.getDouble("maxcollect")} " +
                        "where uuid = '$uuid'")){
                rs.close()
                mysql.close()
                return false
            }

        }else{
            if (!mysql.execute("insert into coinflip_player_log (uuid, mcid, win, lose, wincollect, losecollect, maxcollect) VALUES " +
                        "('${uuid}', '${Bukkit.getOfflinePlayer(uuid).name}', ${if (win) 1 else 0}, ${if (win) 0 else 1}, ${if (win) bet else 0}, ${if (win) 0 else bet}, ${bet})")){
                rs?.close()
                mysql.close()
                return false
            }
        }

        rs?.close()
        mysql.close()
        return true
    }

    //メインの処理
    override fun run() {

        val name = data.entries.first().value.name//募集者の名前
        vault.withdraw(playeruuid,bet)//募集者からお金を引く



        for (time in 60 downTo 0){

            if (data.size == 2){//=二人のプレイヤーがそろう
                break
            }

            if (time == 0){
                broadcast("§l${name}§aの§eCoinFlip§aは人が集まらなかったので中止しました")
                data.clear()
                CoinFlip.coinFlipData.remove(playeruuid)//データ削除
                vault.deposit(playeruuid,bet)//返金
                return
            }
            sleep(1000)

            if (time % 10 == 0){
                Bukkit.broadcast(runcmd("§l${name}§aが§b${if (heads) "§c§l表" else "§b§l裏"}の予想§aで§e§lCoinFlip§aを募集中...残り${time}秒\n" +
                        "§f/cf join $name §e必要金額 $bet ${if (!heads) "§c§l表" else "§b§l裏"}§eだと思う人は参加してみよう！","/cf join $name","§6またはここをクリック！"), Server.BROADCAST_CHANNEL_USERS)
            }

        }

        val startPlayerData = data.entries.first().value//募集者のPlayerData
        val joinPlayerData = data.entries.last().value//参加者のPlayerData too
        val startUUID = data.entries.first().key//募集者のuuid
        val joinUUID = data.entries.last().key//参加者のuuid

        //参加者の頭
        startPlayerData.gui.setItem(26,
            SInventoryItem(SItemStack(Material.PLAYER_HEAD).setHeadOwner(joinUUID)
                .setLore(arrayListOf(if (heads) "予想：§4裏" else "予想：§4表")).build()).clickable(false))

        //募集者の頭
        joinPlayerData.gui.setItem(26,
            SInventoryItem(SItemStack(Material.PLAYER_HEAD).setHeadOwner(startUUID)
                .setLore(arrayListOf(if (!heads) "予想：§4裏" else "予想：§4表")).build()).clickable(false))

        runTask {//invを開かせる
            startPlayerData.gui.open(player(startUUID))
            joinPlayerData.gui.open(player(joinUUID))
        }

        val win = Random.nextInt(0..1) == 0 //0なら表の勝利、それ以外なら裏の勝利
        val loop = if (win) Random.nextInt(7,10)*2 else Random.nextInt(7,10)*2+1

        allPlayerSend("§eコインを投げています...")


        for (i in 1..loop){//loop処理

            startPlayerData.gui.setItem(13,if (i % 2 == 0) CoinFlip.heads else CoinFlip.tails)//交互にheadsとtailsを切り替える
            joinPlayerData.gui.setItem(13,if (i % 2 == 0) CoinFlip.heads else CoinFlip.tails)//too
            startPlayerData.gui.renderInventory()//invの再読み込み
            joinPlayerData.gui.renderInventory()//too
            allPlaySound(Sound.BLOCK_ANVIL_PLACE,0.5f,2f)//音をならす いい音が見つからない
            sleep(500)//0.5秒待つ
        }

        val headswin: Boolean

        if (win){//表
            headswin = true
            allPlayerSend("§e§l${if (heads) startPlayerData.name else joinPlayerData.name}§aが§c§l表§eの予想を当てました！")
            allPlaySound(Sound.ENTITY_PLAYER_LEVELUP,1f,1f)
            vault.deposit(if (heads) startUUID else joinUUID,bet*2)//賞金
            broadcast("§e§l${if (heads) startPlayerData.name else joinPlayerData.name}§aは§e§lCoinFlip§aで§c§l表§eの予想を当て${bet*2}円を獲得した！")
        }else{//裏
            headswin = false
            allPlayerSend("§e§l${if (!heads) startPlayerData.name else joinPlayerData.name}§aが§b§l裏§eの予想を当てました！")
            allPlaySound(Sound.ENTITY_PLAYER_LEVELUP,1f,1f)
            vault.deposit(if (!heads) startUUID else joinUUID,bet*2)//賞金
            broadcast("§e§l${if (!heads) startPlayerData.name else joinPlayerData.name}§aは§e§lCoinFlip§aで§b§l裏§eの予想を当て${bet*2}円を獲得した！")
        }


        //試合のログを保存
        mysql.execute("insert into coinflip_data_log (startUUID, startMCID, joinUUID, joinMCID, bet, win) VALUES " +
                "('$startUUID', '${startPlayerData.name}', '${joinUUID}', '${joinPlayerData.name}', $bet, '${if (headswin) startPlayerData.name else joinPlayerData.name}')")
        mysql.close()

        savePlayerLog(startUUID,headswin == heads)
        savePlayerLog(joinUUID,headswin == !heads)

        sleep(2000)
        runTask {//inv close
            player(startUUID)?.closeInventory()
            player(joinUUID)?.closeInventory()
        }

        data.clear()//データ削除
        CoinFlip.coinFlipData.remove(playeruuid)//too
        return
    }
}