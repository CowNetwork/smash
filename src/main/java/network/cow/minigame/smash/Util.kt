package network.cow.minigame.smash

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.pow
import kotlin.math.roundToInt

fun destroyAndReplaceBlockByBlock(player: Player) {
    val prev = mutableListOf<BlockState>()
    val blocks = getNearbyBlocks(player.location, 3)

    // can be empty since getNearbyBlocks can be triggered if only air surrounds the player
    if (blocks.isEmpty()) return

    getNearbyBlocks(player.location, 3).forEach {
        prev.add(BlockState(it.location, it.type, it.blockData))
        val falling = it.world.spawnFallingBlock(it.location, it.blockData)
        falling.setHurtEntities(false)
        falling.dropItem = false
        it.type = Material.AIR
    }

    player.world.playSound(player.location, Sound.ENTITY_WITHER_BREAK_BLOCK, 1.0f, 1.0f)
    RebuildTask(prev.iterator()).runTaskTimer(JavaPlugin.getPlugin(SmashPlugin::class.java), 20, 20)
}

fun getNearbyBlocks(location: Location, radius: Int): List<Block> {
    val blocks = mutableListOf<Block>()
    for (x in location.blockX - radius..location.blockX + radius) {
        for (y in location.blockY - radius..location.blockY + radius) {
            for (z in location.blockZ - radius..location.blockZ + radius) {
                val block = location.world.getBlockAt(x, y, z)
                val dist = location.distanceSquared(block.location)
                val probability = 1.0 - dist / radius.toDouble().pow(2)
                if (Math.random() <= probability) {
                    if (block.type == Material.AIR) continue
                    blocks.add(block)
                }
            }
        }
    }
    return blocks
}