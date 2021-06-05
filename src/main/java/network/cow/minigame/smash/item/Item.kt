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
package network.cow.minigame.smash.item

import network.cow.minigame.smash.SmashPlugin
import network.cow.minigame.smash.event.ItemRemoveEvent
import network.cow.minigame.smash.setDropLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.EulerAngle
import java.util.*

abstract class Item : Listener {

    val id = UUID.randomUUID()

    open fun spawn(location: Location) {
        this.itemStack().setDropLocation(location)
        location.world.dropItem(location, this.itemStack())
    }

    abstract fun use(user: Player, affected: List<Player>)

    abstract fun itemStack(): ItemStack

    open fun onPickUp(player: Player) = Unit

    open fun remove(user: Player) {
        HandlerList.unregisterAll(this)
        user.inventory.remove(this.itemStack())
        // fire this event to let the item manager know, that this item can be removed
        // from the internal map. Every item needs to take care of this themselves.
        Bukkit.getPluginManager().callEvent(ItemRemoveEvent(this))
    }

    fun register() {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getPlugin(SmashPlugin::class.java))
    }
}