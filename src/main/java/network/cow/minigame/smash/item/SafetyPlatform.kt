package network.cow.minigame.smash.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import network.cow.minigame.smash.StateKey
import network.cow.minigame.smash.setSmashState
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SafetyPlatform(val radius: Int, val removeAfter: Int) : Item() {

    private lateinit var handle: ItemStack

    override fun spawn(location: Location) {
        location.world.dropItem(location, this.itemStack())
    }

    override fun use(user: Player, affected: List<Player>) {

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

    override fun onPickUp(player: Player) = Unit
}