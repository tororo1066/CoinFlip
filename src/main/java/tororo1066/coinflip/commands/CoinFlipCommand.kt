package tororo1066.coinflip.commands

import tororo1066.coinflip.CoinFlip
import tororo1066.coinflip.Utils.SCommandRouter.*

class CoinFlipCommand : SCommandRouter() {

    init {
        pluginPrefix = "§f[§e§lCoinFlip§f]§r"
        registerCommands()
        registerEvents()
    }


    fun registerEvents() {
        setNoPermissionEvent { e: SCommandData -> CoinFlip.sendMsg(e.sender,"§4権限がありません") }
        setOnNoCommandFoundEvent { e: SCommandData -> CoinFlip.sendMsg(e.sender,"§4コマンドが存在しません")}
    }

    fun registerCommands(){

        //cf create <bet> <heads or tails>
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("create")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.DOUBLE).addAlias("賭け金")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.HEADSORTAILS)).
        addRequiredPermission("coinflip.user").addExplanation("CoinFlipの部屋を作成する").
        setExecutor(CreateRoom()))

        //cf join <name>
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("join")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.ONLINE_PLAYER).addAlias("プレイヤー名")).
        addRequiredPermission("coinflip.user").addExplanation("CoinFlipの部屋に参加する").
        setExecutor(JoinRoom()))

        //cf log
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("log")).
        addRequiredPermission("coinflip.log").addExplanation("CoinFlipのログを見る").
        setExecutor(ShowLog()))

        //cf logop <name>
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("logop")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.STRING).addAlias("プレイヤー名")).
        addRequiredPermission("coinflip.op").addExplanation("指定したプレイヤーのログを見る").
        setExecutor(ShowLogOp()))

        //cf mode <true or false>
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("mode")).
        addArgument
        (SCommandArgument().addAllowedType(SCommandArgumentType.BOOLEAN)).
        addRequiredPermission("coinflip.op").addExplanation("モードを切り替える").
        setExecutor(SwitchMode()))

        //cf price <Double> <Double>
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("price")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.DOUBLE).addAlias("最低金額")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.DOUBLE).addAlias("最高金額")).
        addRequiredPermission("coinflip.op").addExplanation("金額の指定").
        setExecutor(PriceCommand()))
    }
}