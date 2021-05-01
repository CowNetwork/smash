package network.cow.minigame.smash.config

import org.bukkit.Location

class Config(
    val baseKnockback: Double, // base knockback
    val itemSpawnLocations: List<Location>,
    val itemSpawnerDelay: Int,
    val itemSpawnerInterval: Int,
    val itemsPerInterval: Int, // how many items should be dropped per interval
    val items: List<ItemConfig> // config for all items
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Config {
            return
        }
    }
}