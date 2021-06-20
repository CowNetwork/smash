/*
    Smash mini game spigot plugin
    Copyright (C) 2021  Yannic Rieger

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package network.cow.mc.minigame.smash.item

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