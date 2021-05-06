package network.cow.minigame.smash.listener

import net.kyori.adventure.text.Component
import network.cow.minigame.smash.SmashPlugin
import network.cow.minigame.smash.setCanUseUnstuckCommand
import network.cow.minigame.smash.setSmashState
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.plugin.java.JavaPlugin

class CancelListener : Listener {

    @EventHandler
    private fun on(e: EntityDamageEvent) {
        if (e.entity !is Player) return
        val player = e.entity as Player
        if (e.cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
            player.sendMessage(Component.text("suffocating in a wall? use /unstuck to return"))
            player.setCanUseUnstuckCommand(true)
            // Disable after 10 seconds to prevent abuse. Doing this using the Bukkit scheduler
            // could lead to a performance impact if a player just keeps suffocating since
            // more and more tasks would be scheduled, but I will keep it like this for now.
            // If we notice significant issues when the game is live, I will change it.
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(SmashPlugin::class.java), Runnable {
                player.setCanUseUnstuckCommand(false)
            }, 20 * 10)
        }
        e.damage = 0.0
    }

    @EventHandler
    private fun on(e: FoodLevelChangeEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun on(e: BlockIgniteEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun on(e: InventoryClickEvent) {
        // cancel inventory click events so players cant move items around
        e.isCancelled = true
    }

    @EventHandler
    private fun on(e: PlayerItemDamageEvent) {
        // add items to ignore here
        // FLINT_AND_STEEL: used as JET_PACK item, item damage indicates how much uses are left
        if (e.item.type == Material.FLINT_AND_STEEL) return
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