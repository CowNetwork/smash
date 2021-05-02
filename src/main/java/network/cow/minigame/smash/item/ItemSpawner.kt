package network.cow.minigame.smash.item

import network.cow.minigame.smash.config.ItemConfig
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable

// Einstellung: alle 2 mins. 2 items

class ItemSpawner(
    private val itemNum: Int,
    private val itemConfigs: List<ItemConfig>,
    private val locations: List<Location>,
    private val types: List<ItemType>,
    private val itemManger: ItemManger
) : BukkitRunnable() {

    override fun run() {
        println("SPAWN $itemNum")
        for (i in 0 until itemNum) {
            val loc = locations.random()
            val type = types.random()
            this.itemConfigs.find { it.type == type }?.let {
                val item = itemManger.createItem(type, it)
                println("SPAWNED ${item.id} at $loc")
                item.spawn(loc)
            }
        }
    }
}