package network.cow.minigame.smash.item

import network.cow.minigame.smash.SmashPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

abstract class Item : Listener {

    val id = UUID.randomUUID()

    abstract fun spawn(location: Location)

    abstract fun use(user: Player, affected: List<Player>)

    abstract fun itemStack(): ItemStack

    open fun remove(user: Player) {
        HandlerList.unregisterAll(this)
        user.inventory.remove(this.itemStack())
    }

    fun register() {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getPlugin(SmashPlugin::class.java))
    }
}