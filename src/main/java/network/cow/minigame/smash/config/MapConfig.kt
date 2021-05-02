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