package network.cow.minigame.smash.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import network.cow.minigame.smash.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import java.util.*
import kotlin.math.roundToInt

class JetPack(val uses: Int) : Item() {

    private lateinit var handle: ItemStack

    override fun spawn(location: Location) {
        location.world.dropItem(location, this.itemStack())
    }

    override fun onPickUp(player: Player) {
        player.setJetPackUses(uses)
    }

    override fun use(user: Player, affected: List<Player>) {
        user.playSound(user.location, Sound.ENTITY_CAT_HISS, .5f, 1.0f)
        user.velocity = user.location.direction.setY(1).normalize().multiply(1.5)

        val remainingUses = user.getJetPackUses().dec()
        user.setJetPackUses(remainingUses)
        val percentage = remainingUses / this.uses.toDouble()

        val meta = this.handle.itemMeta as Damageable
        meta.damage = (this.handle.type.maxDurability * (1.0 - percentage)).roundToInt()
        this.handle.itemMeta = meta as ItemMeta

        user.inventory.setItemInMainHand(this.handle) // update item

        if  (user.getJetPackUses() == 0) {
            this.remove(user)
            return
        }
    }

    override fun remove(user: Player) {
        super.remove(user)
        user.setJetPackUses(0)
    }

    override fun itemStack(): ItemStack {
        if (this::handle.isInitialized) {
            return this.handle
        }
        this.handle = ItemStack(Material.FLINT_AND_STEEL)
        val meta = handle.itemMeta
        meta.displayName(Component.text("Jet Pack"))
        meta.lore(listOf(Component.text(this.id.toString()).color(NamedTextColor.BLACK)))
        handle.itemMeta = meta
        handle.setSmashState(StateKey.ITEM_ID, this.id)
        return handle
    }

    @EventHandler
    private fun onPlayerInteract(event: PlayerInteractEvent) {
        // since we change the itemstack we need to have a isSimilar check here
        // because the ItemStack.getState is lost
        if (event.item?.isSimilar(this.handle) != true) return
        this.use(event.player, listOf())
    }
}