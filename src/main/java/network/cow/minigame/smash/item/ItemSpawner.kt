package network.cow.minigame.smash.item

import network.cow.minigame.smash.SmashPlugin
import network.cow.minigame.smash.config.ItemConfig
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

// Einstellung: alle 2 mins. 2 items

class ItemSpawner(
    private val itemNum: Int,
    private val itemConfigs: List<ItemConfig>,
    private val locations: List<Location>,
    private val types: List<ItemType>,
    private val maxConcurrentItems: Int,
    private val itemManger: ItemManger
) : BukkitRunnable() {
    override fun run() {
        for (i in 0 until itemNum) {
            // everytime we want to spawn an item, check if there are enough items
            if (this.itemManger.getItemsInGame().size == maxConcurrentItems) {
                return
            }

            val loc = locations.random()
            val type = types.random()

            if (this.itemManger.isItemAt(loc)) return

            this.itemConfigs.find { it.type == type }?.let {
                val item = this.itemManger.createItem(type, it)
                this.itemManger.itemDroppedAt(loc)
                item.spawn(loc)
            }
        }
    }
}