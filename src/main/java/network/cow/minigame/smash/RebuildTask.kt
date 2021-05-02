package network.cow.minigame.smash

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.data.BlockData
import org.bukkit.scheduler.BukkitRunnable

class BlockState(val location: Location, val type: Material, val blockData: BlockData)

class RebuildTask(private val iter: Iterator<BlockState>) : BukkitRunnable() {
    override fun run() {
        if (!iter.hasNext()) {
            this.cancel()
            return
        }
        val state = iter.next()
        state.location.block.type = state.type
        state.location.block.blockData = state.blockData
        state.location.world.playSound(state.location, Sound.BLOCK_CHAIN_PLACE, 0.5f, 1.0f)
        state.location.world.spawnParticle(
            Particle.CLOUD,
            state.location.x,
            state.location.y + .5,
            state.location.z,
            3,
            0.3,
            0.3,
            0.3,
            0.0,
        )
    }
}