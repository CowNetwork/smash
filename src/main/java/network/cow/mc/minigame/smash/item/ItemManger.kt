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

import network.cow.minigame.smash.config.Config
import network.cow.minigame.smash.config.ItemConfig
import org.bukkit.Location
import org.bukkit.util.Vector
import java.lang.IllegalArgumentException
import java.util.*

class ItemManger(private val config: Config) {
    private val items: MutableMap<UUID, Item> = mutableMapOf()
    private val droppedAt: MutableList<Vector> = mutableListOf()

    fun createItem(type: ItemType, itemConfig: ItemConfig): Item {
        return when (type) {
            ItemType.HAMMER -> {
                val item = Hammer(itemConfig.data["baseKnockbackMultiplier"] as Double, config.baseKnockback)
                this.items[item.id] = item
                item
            }
            ItemType.LESSER_DAMAGE_REMOVAL_FOOD -> {
                val item = LesserDamageRemovalFood(itemConfig.data["damageRemoved"] as Double)
                this.items[item.id] = item
                item
            }
            ItemType.BIG_DAMAGE_REMOVAL_FOOD -> {
                val item = BigDamageRemovalFood(itemConfig.data["damageRemoved"] as Double)
                this.items[item.id] = item
                item
            }
            ItemType.JET_PACK -> {
                val item = JetPack(itemConfig.data["uses"] as Int)
                this.items[item.id] = item
                item
            }
            ItemType.HAND_GRENADE -> {
                val item = Handgrenade(
                    itemConfig.data["radius"] as Double,
                    itemConfig.data["baseKnockbackMultiplier"] as Double,
                    config.baseKnockback
                )
                this.items[item.id] = item
                item
            }
            ItemType.SAFETY_PLATFORM -> {
                val item = SafetyPlatform(
                    itemConfig.data["radius"] as Int,
                    itemConfig.data["removeAfter"] as Int
                )
                this.items[item.id] = item
                item
            }
            else -> throw IllegalArgumentException("lol")
        }
    }

    fun getItemById(id: UUID): Item? {
        return this.items[id]
    }

    fun getItemsInGame(): List<Item> {
        return this.items.values.toList()
    }

    fun itemDroppedAt(loc: Location) {
        this.droppedAt.add(loc.toVector())
    }

    fun isItemAt(location: Location): Boolean {
        return this.droppedAt.contains(location.toVector())
    }

    fun pickedUpAt(location: Location) {
        this.droppedAt.remove(location.toVector())
    }

    fun removeItem(id: UUID) {
        this.items.remove(id)
    }
}