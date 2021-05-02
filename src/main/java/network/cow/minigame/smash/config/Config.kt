package network.cow.minigame.smash.config

import network.cow.minigame.smash.item.ItemType
import org.bukkit.configuration.MemorySection

class Config(
    val baseKnockback: Double, // base knockback
    val itemSpawnerDelay: Int,
    val itemSpawnerInterval: Int,
    val itemsPerInterval: Int, // how many items should be dropped per interval,
    val livesPerPlayer: Int,
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
                items
            )
        }
    }
}