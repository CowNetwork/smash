package network.cow.minigame.smash.listener

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerItemDamageEvent

class CancelListener : Listener {

    @EventHandler
    private fun on(e: EntityDamageEvent) {
        if (e.entity !is Player) return
        e.damage = 0.0
    }

    @EventHandler
    private fun on(e: PlayerItemDamageEvent) {
        e.damage = 0
        e.isCancelled = true
    }

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