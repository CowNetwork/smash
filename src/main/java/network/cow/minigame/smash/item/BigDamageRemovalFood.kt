package network.cow.minigame.smash.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import network.cow.minigame.smash.StateKey
import network.cow.minigame.smash.getSmashState
import network.cow.minigame.smash.removeDamagePercentage
import network.cow.minigame.smash.setSmashState
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class BigDamageRemovalFood(val damageRemoved: Double) : Item() {

    private lateinit var handle: ItemStack

    override fun use(user: Player, affected: List<Player>) {
        user.removeDamagePercentage(this.damageRemoved)
        user.location.world.spawnParticle(
            Particle.VILLAGER_HAPPY,
            user.location.x,
            user.location.y + .5,
            user.location.z,
            6,
            0.3,
            0.3,
            0.3,
            0.0,
        )
        this.remove(user)
        user.playSound(user.location, Sound.ENTITY_PLAYER_BURP, .5f, .5f)
    }

    override fun itemStack(): ItemStack {
        if (this::handle.isInitialized) {
            return handle
        }
        val food = listOf(
            Material.GOLDEN_APPLE,
            Material.BEETROOT_SOUP,
        )
        handle = ItemStack(food.random())
        val meta = handle.itemMeta
        meta.lore(listOf(Component.text(this.id.toString()).color(NamedTextColor.BLACK)))
        handle.itemMeta = meta
        handle.setSmashState(StateKey.ITEM_ID, this.id)
        return handle
    }

    @EventHandler
    private fun onPlayerInteract(event: PlayerInteractEvent) {
        // we still want to be able to hit players without using it
        if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) return
        val itemId: UUID? = event.player.inventory.itemInMainHand.getSmashState(StateKey.ITEM_ID)
        if (this.id != itemId) return
        this.use(event.player, listOf())
    }
}