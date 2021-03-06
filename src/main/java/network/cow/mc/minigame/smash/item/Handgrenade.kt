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
package network.cow.mc.minigame.smash.item

import net.kyori.adventure.sound.SoundStop.source
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import network.cow.minigame.smash.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector


class Handgrenade(val radius: Double, val baseKnockbackMultiplier: Double, val baseKnockback: Double) : Item() {

    private lateinit var handle: ItemStack
    private lateinit var impactLocation: Location

    override fun use(user: Player, affected: List<Player>) {
        impactLocation.world.playSound(impactLocation, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1f)
        affected.forEach {
            val deltaX = it.location.x - this.impactLocation.x
            val deltaZ = it.location.z - this.impactLocation.z
            val vec = Vector(deltaX, 1.0, deltaZ)
            // TODO: particle effects
            it.setHitter(network.cow.mc.minigame.smash.Hitter(user, ItemType.HAND_GRENADE))
            it.knockback(vec, this.baseKnockbackMultiplier * this.baseKnockback)
        }

        impactLocation.world.spawnParticle(
            Particle.CLOUD,
            impactLocation.x,
            impactLocation.y + .5,
            impactLocation.z,
            20,
            1.5,
            1.5,
            1.5,
            .2,
        )

        this.remove(user)
    }

    override fun itemStack(): ItemStack {
        if (this::handle.isInitialized) {
            return this.handle
        }
        this.handle = ItemStack(Material.SNOWBALL)
        val meta = handle.itemMeta
        meta.displayName(Component.text("Handgrenade"))
        meta.lore(listOf(Component.text(this.id.toString()).color(NamedTextColor.BLACK)))
        handle.itemMeta = meta
        handle.setSmashState(StateKey.ITEM_ID, this.id)
        return handle
    }

    @EventHandler
    private fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        if (event.entityType != EntityType.SNOWBALL) return
        val stack = ((event.entity) as Snowball).item
        if (!stack.isSimilar(this.handle)) return
        val shooter = event.entity.shooter as Player
        shooter.playSound(shooter.location, Sound.ENTITY_TNT_PRIMED, 1f, 1f)
    }

    @EventHandler
    private fun onProjectileHit(event: ProjectileHitEvent) {
        if (event.entityType != EntityType.SNOWBALL) return
        val stack = ((event.entity) as Snowball).item
        if (!stack.isSimilar(this.handle)) return
        val shooter = event.entity.shooter as Player

        val location = when {
            event.hitEntity != null -> event.hitEntity!!.location
            event.hitBlock != null -> event.hitBlock!!.location
            else -> Location(event.entity.world, 0.0, 0.0, 0.0)
        }

        this.impactLocation = location

        val affected = location.getNearbyLivingEntities(this.radius)
            .filterIsInstance<Player>()
            .filter { it != shooter }

        this.use(shooter, affected)
    }
}