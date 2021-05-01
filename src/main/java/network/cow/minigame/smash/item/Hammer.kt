package network.cow.minigame.smash.item

import network.cow.minigame.smash.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class Hammer(val knockbackMultiplierPercentage: Double, val baseKnockback: Double) : Item() {

    private lateinit var handle: ItemStack

    override fun spawn(location: Location) {
        location.world.dropItem(location, this.itemStack())
    }

    override fun use(user: Player, affected: List<Player>) {
        val add = baseKnockback * knockbackMultiplierPercentage
        affected.first().knockback(user.location.clone().direction, baseKnockback + add)
    }

    override fun itemStack(): ItemStack {
        if (this::handle.isInitialized) {
            return handle
        }
        handle = ItemStack(Material.NETHERITE_AXE)
        handle.setSmashState(StateKey.ITEM_ID, this.id)
        return handle
    }

    @EventHandler
    private fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.damager !is Player || event.entity !is Player) return
        val damager = event.damager as Player

        val itemId: UUID? = damager.inventory.itemInMainHand.getSmashState(StateKey.ITEM_ID)
        if (id != itemId) return

        this.use(damager, listOf(event.entity as Player))

    }
}