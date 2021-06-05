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
package network.cow.minigame.smash.config

import network.cow.minigame.noma.api.get
import network.cow.minigame.noma.spigot.SpigotGame
import network.cow.minigame.noma.spigot.pool.SpawnLocation
import network.cow.minigame.noma.spigot.pool.WorldMeta
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

class MapConfig(val itemSpawnLocations: List<Location>, val playerSpawnLocations: List<Location>) {
    companion object {
        fun from(world: World, worldMeta: WorldMeta): MapConfig {
            val itemSpawnLocations = (worldMeta.options["itemSpawnLocations"] as List<Map<String, Any>>).map {
                readLocation(world, it)
            }.toList()
            return MapConfig(
                itemSpawnLocations,
                worldMeta.globalSpawnLocations.map { it.toLocation(world) }.toList()
            )
        }

        private fun readLocation(world: World, map: Map<String, Any>): Location {
            val loc = Location(
                world,
                map.get("x", 0.0),
                map.get("y", 0.0),
                map.get("z", 0.0)
            )
            loc.yaw = map.get("yaw", 0.0F)
            loc.pitch = map.get("pitch", 0.0F)
            return loc
        }
    }
}