package network.cow.minigame.smash.listener

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityChangeBlockEvent

class CancelListener : Listener {

    @EventHandler
    private fun on(e: EntityChangeBlockEvent) {
        if (e.entityType != EntityType.FALLING_BLOCK) {
            return
        }

        e.block.type = Material.AIR
        e.isCancelled = true
    }

    @EventHandler
    private fun on(e: BlockBreakEvent) {
        e.isDropItems = false
    }
}