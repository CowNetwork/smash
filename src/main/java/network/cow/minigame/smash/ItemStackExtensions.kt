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
package network.cow.minigame.smash

import network.cow.spigot.extensions.state.getState
import network.cow.spigot.extensions.state.setState
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import java.util.*

fun ItemStack.getDropLocation(): Location? {
    return this.getSmashState(StateKey.ITEM_DROP_LOCATION)
}

fun ItemStack.setDropLocation(location: Location) {
    this.setSmashState(StateKey.ITEM_DROP_LOCATION, location)
}

fun ItemStack.getSmashId(): UUID? {
    return this.getSmashState(StateKey.ITEM_ID)
}

fun ItemStack.setSmashState(key: StateKey, value: Any) {
    this.setState(SmashPlugin::class.java, key.key, value)
}

fun <T> ItemStack.getSmashState(key: StateKey): T? {
    return this.getState(SmashPlugin::class.java, key.key)
}

fun <T> ItemStack.getSmashState(key: StateKey, default: T): T {
    return this.getState(SmashPlugin::class.java, key.key, default = default!!)
}