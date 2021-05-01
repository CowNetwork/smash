package network.cow.minigame.smash.item

import network.cow.minigame.smash.config.Config
import network.cow.minigame.smash.config.ItemConfigEntry
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable

// Einstellung: alle 2 mins. 2 items

class ItemSpawner(
    private val itemNum: Int,
    private val itemConfigs: List<ItemConfigEntry>,
    private val locations: List<Location>,
    private val types: List<ItemType>,
    private val itemManger: ItemManger
) : BukkitRunnable() {

    override fun run() {
        for (i in 0 until itemNum) {
            val loc = locations.random()
            val type = types.random()
            this.itemConfigs.find { it.type == type }?.let {
                val item = itemManger.createItem(type, it)
                item.spawn(loc)
            }
        }
    }
}