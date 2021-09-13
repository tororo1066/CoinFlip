package tororo1066.coinflip

import tororo1066.coinflip.CoinFlip.Companion.mysql
import java.util.*
import java.util.concurrent.Callable
import kotlin.math.floor

class CoinFlipLog{

    lateinit var uuid : UUID
    lateinit var mcid : String
    var win = 0
    var lose = 0
    var wincollect = 0.0
    var losecollect = 0.0
    var maxcollect = 0.0
    var winingpersentage = 0.0
    var success = false


    /**
     * ログを表示させる
     * @param uuid プレイヤーのuuid
     * @return logのデータ 存在しない場合は空っぽ
     */
    fun showlog(uuid : UUID) : CoinFlipLog{

        val logdata = CoinFlipLog()

        CoinFlip.es.execute {
            val rs = mysql.query("select * from coinflip_player_log where uuid = '$uuid'")
            if (rs == null){
                mysql.close()
                return@execute
            }
            if (!rs.next()) {
                rs.close()
                mysql.close()
                return@execute
            }
            logdata.uuid = UUID.fromString(rs.getString("uuid"))
            logdata.mcid = rs.getString("mcid")
            logdata.win = rs.getInt("win")
            logdata.lose = rs.getInt("lose")
            logdata.wincollect = rs.getDouble("wincollect")
            logdata.losecollect = rs.getDouble("losecollect")
            logdata.maxcollect = rs.getDouble("maxcollect")
            if (logdata.win == 0 || logdata.lose == 0){
                logdata.winingpersentage = 0.0
            }else{
                logdata.winingpersentage = floor((logdata.win.toDouble() / (logdata.win.toDouble() + logdata.lose.toDouble())) * 100)
            }
            logdata.success = true

            rs.close()
            mysql.close()

            return@execute
        }
        return logdata
    }

}