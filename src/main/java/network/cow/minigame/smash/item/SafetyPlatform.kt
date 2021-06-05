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
package network.cow.minigame.smash.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import network.cow.minigame.smash.BlockState
import network.cow.minigame.smash.SmashPlugin
import network.cow.minigame.smash.StateKey
import network.cow.minigame.smash.setSmashState
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import javax.lang.model.element.VariableElement
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class SafetyPlatform(val radius: Int, val removeAfter: Int) : Item() {

    private lateinit var handle: ItemStack

    override fun use(user: Player, affected: List<Player>) {
        val blocks = mutableListOf<BlockState>()
        user.velocity = Vector(0, 0, 0)
        for (x in -radius until radius) {
            for (z in -radius until radius) {
                val loc = user.location.clone().add(x.toDouble(), -2.0, z.toDouble())
                blocks.add(BlockState(loc, loc.block.type, loc.block.blockData))
                loc.block.type = Material.LIME_STAINED_GLASS
            }
        }

        this.remove(user)
        user.playSound(user.location, Sound.BLOCK_GILDED_BLACKSTONE_PLACE, 1f, 1f)

        // indicates to the player that 3/4 of the time is over and the platform will be removed soon
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(SmashPlugin::class.java), Runnable {
            blocks.forEach {
                it.location.block.type = Material.RED_STAINED_GLASS
            }
        }, ((removeAfter * 20) * 0.75).roundToLong())

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(SmashPlugin::class.java), Runnable {
            // play sound for all players near location so everyone knows that the platform has been removed
            blocks.first().location.world.playSound(blocks.first().location, Sound.BLOCK_NETHERRACK_BREAK, 1f, 1f)
            blocks.forEach {
                it.location.block.type = it.type
                it.location.block.blockData = it.blockData
                it.location.world.spawnParticle(
                    Particle.CLOUD,
                    it.location.x,
                    it.location.y + .5,
                    it.location.z,
                    6,
                    0.3,
                    0.3,
                    0.3,
                    0.0,
                )
            }
        }, removeAfter.toLong() * 20)
    }

    override fun itemStack(): ItemStack {
        if (this::handle.isInitialized) {
            return handle
        }
        handle = ItemStack(Material.SLIME_BALL)
        val meta = handle.itemMeta
        meta.displayName(Component.text("Safety platform"))
        meta.lore(listOf(Component.text(this.id.toString()).color(NamedTextColor.BLACK)))
        handle.itemMeta = meta
        handle.setSmashState(StateKey.ITEM_ID, this.id)
        return handle
    }

    @EventHandler
    private fun onInventoryClick(event: PlayerInteractEvent) {
        // we still want to be able to hit players without using it
        if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) return

        // since we change the itemstack we need to have a isSimilar check here
        // because the ItemStack.getState is lost
        if (event.item?.isSimilar(this.handle) != true) return
        this.use(event.player, listOf())
    }
}