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
import network.cow.minigame.smash.*
import network.cow.minigame.smash.event.ItemRemoveEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class Hammer(val baseKnockbackMultiplier: Double, val baseKnockback: Double) : Item() {

    private lateinit var handle: ItemStack

    override fun use(user: Player, affected: List<Player>) {
        val power = baseKnockback * baseKnockbackMultiplier
        affected.first().setHitter(Hitter(user, ItemType.HAMMER))
        affected.first().knockback(user.location.clone().direction, power)
        this.remove(user)
    }

    override fun itemStack(): ItemStack {
        // every item has to make sure that only one ItemStack instance representing it exists.
        if (this::handle.isInitialized) {
            return handle
        }
        handle = ItemStack(Material.NETHERITE_AXE)
        val meta = handle.itemMeta
        meta.isUnbreakable = true
        meta.displayName(Component.text("hammer"))
        meta.lore(listOf(Component.text(this.id.toString()).color(NamedTextColor.BLACK)))
        handle.itemMeta = meta
        handle.setSmashState(StateKey.ITEM_ID, this.id)
        return handle
    }

    @EventHandler
    private fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.damager !is Player || event.entity !is Player) return
        val damager = event.damager as Player
        val itemId: UUID? = damager.inventory.itemInMainHand.getSmashState(StateKey.ITEM_ID)
        if (this.id != itemId) return

        this.use(damager, listOf(event.entity as Player))
    }
}