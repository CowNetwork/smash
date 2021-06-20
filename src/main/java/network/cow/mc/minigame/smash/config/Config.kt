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
package network.cow.mc.minigame.smash.config

import network.cow.minigame.smash.item.ItemType
import org.bukkit.configuration.MemorySection

class Config(
    val baseKnockback: Double, // base knockback
    val itemSpawnerDelay: Int,
    val itemSpawnerInterval: Int,
    val itemsPerInterval: Int, // how many items should be dropped per interval,
    val livesPerPlayer: Int,
    val maxConcurrentItems: Int,
    val playerPickUpCooldown: Int,
    val items: List<ItemConfig> // config for all items
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Config {
            val spawnerSection = map["spawner"] as MemorySection

            val items = (map["items"] as List<Map<String, Any>>).map {
                ItemConfig(ItemType.valueOf(it["type"].toString()), it["data"] as Map<String, Any>)
            }.toList()
            return Config(
                map["baseKnockback"] as Double,
                spawnerSection["delay"] as Int,
                spawnerSection["interval"] as Int,
                spawnerSection["itemsPerInterval"] as Int,
                map["livesPerPlayer"] as Int,
                map["maxConcurrentItems"] as Int,
                map["playerPickUpCooldown"] as Int,
                items
            )
        }
    }
}