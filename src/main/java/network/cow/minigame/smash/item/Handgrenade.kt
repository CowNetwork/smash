package network.cow.minigame.smash.item

import net.kyori.adventure.sound.SoundStop.source
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import network.cow.minigame.smash.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector


class Handgrenade(val radius: Double, val baseKnockbackMultiplier: Double, val baseKnockback: Double) : Item() {

    lateinit var handle: ItemStack

    override fun spawn(location: Location) {
        location.world.dropItem(location, this.itemStack())
    }

    override fun use(user: Player, affected: List<Player>) = Unit

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

    override fun onPickUp(player: Player) = Unit

    @EventHandler
    private fun onProjectileHit(event: ProjectileHitEvent) {
        if (event.entityType != EntityType.SNOWBALL) return
        val stack = ((event.entity) as Snowball).item
        if (!stack.isSimilar(this.handle)) return
        val shooter = event.entity.shooter as Player

        // remove here because otherwise EventHandler would be unregistered _before_
        // before the snowball hits
        this.remove(shooter)

        val location = when {
            event.hitEntity != null -> event.hitEntity!!.location
            event.hitBlock != null -> event.hitBlock!!.location
            else -> Location(event.entity.world, 0.0, 0.0, 0.0)
        }

        location.getNearbyLivingEntities(this.radius).forEach {
            if (it is Player && it != shooter) {
                val deltaX = it.location.x - location.x
                val deltaZ = it.location.z - location.z
                val vec = Vector(deltaX, 1.0, deltaZ)
                // TODO: sound
                // TODO: particle effects
                it.setHitter(Hitter(event.entity.shooter as Player, ItemType.HAND_GRENADE))
                it.knockback(vec, this.baseKnockbackMultiplier * this.baseKnockback)
            }
        }
    }
}