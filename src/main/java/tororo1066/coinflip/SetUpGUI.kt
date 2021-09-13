package tororo1066.coinflip

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import tororo1066.coinflip.CoinFlip.Companion.heads
import tororo1066.coinflip.Utils.SInventory.SInventory
import tororo1066.coinflip.Utils.SInventory.SInventoryItem
import tororo1066.coinflip.Utils.SItemStack
import tororo1066.coinflip.CoinFlip.Companion.plugin
import java.util.*

/**
 * CoinFlipのGUIを作成する処理
 * @param player そのプレイヤーのuuid
 * @param heads 表ならtrue、裏ならfalse
 */
class SetUpGUI(val player : UUID, private val heads : Boolean) : SInventory("[§e§lCoinFlip§r]",3, plugin){


    init {
        val background = SInventoryItem(SItemStack(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build()).clickable(false)
        fillItem(background)

        setItem(intArrayOf(3,23), SInventoryItem(SItemStack(Material.PINK_STAINED_GLASS_PANE).setDisplayName(" ").build()).clickable(false))
        setItem(intArrayOf(12,14), SInventoryItem(SItemStack(Material.LIME_STAINED_GLASS_PANE).setDisplayName(" ").build()).clickable(false))
        setItem(intArrayOf(21,5), SInventoryItem(SItemStack(Material.WHITE_STAINED_GLASS_PANE).setDisplayName(" ").build()).clickable(false))
        setItem(13, ItemStack(Material.AIR))
        setItem(0,SInventoryItem(SItemStack(Material.PLAYER_HEAD).setHeadOwner(player).setLore(arrayListOf(if (this.heads) "予想：§4表" else "予想：§4裏")).build()).clickable(false))
        setItem(13,CoinFlip.heads)
    }
}