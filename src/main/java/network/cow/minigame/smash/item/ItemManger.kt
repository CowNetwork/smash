package network.cow.minigame.smash.item

import network.cow.minigame.smash.config.Config
import network.cow.minigame.smash.config.ItemConfig
import org.bukkit.Location
import org.bukkit.util.Vector
import java.lang.IllegalArgumentException
import java.util.*

class ItemManger(private val config: Config) {
    private val items: MutableMap<UUID, Item> = mutableMapOf()
    private val droppedAt: MutableList<Vector> = mutableListOf()

    fun createItem(type: ItemType, itemConfig: ItemConfig): Item {
        return when (type) {
            ItemType.HAMMER -> {
                val item = Hammer(itemConfig.data["baseKnockbackMultiplier"] as Double, config.baseKnockback)
                this.items[item.id] = item
                item
            }
            ItemType.LESSER_DAMAGE_REMOVAL_FOOD -> {
                val item = LesserDamageRemovalFood(itemConfig.data["damageRemoved"] as Double)
                this.items[item.id] = item
                item
            }
            ItemType.BIG_DAMAGE_REMOVAL_FOOD -> {
                val item = BigDamageRemovalFood(itemConfig.data["damageRemoved"] as Double)
                this.items[item.id] = item
                item
            }
            ItemType.JET_PACK -> {
                val item = JetPack(itemConfig.data["uses"] as Int)
                this.items[item.id] = item
                item
            }
            ItemType.HAND_GRENADE -> {
                val item = Handgrenade(
                    itemConfig.data["radius"] as Double,
                    itemConfig.data["baseKnockbackMultiplier"] as Double,
                    config.baseKnockback
                )
                this.items[item.id] = item
                item
            }
            ItemType.SAFETY_PLATFORM -> {
                val item = SafetyPlatform(
                    itemConfig.data["radius"] as Int,
                    itemConfig.data["removeAfter"] as Int
                )
                this.items[item.id] = item
                item
            }
            else -> throw IllegalArgumentException("lol")
        }
    }

    fun getItemById(id: UUID): Item? {
        return this.items[id]
    }

    fun getItemsInGame(): List<Item> {
        return this.items.values.toList()
    }

    fun itemDroppedAt(loc: Location) {
        this.droppedAt.add(loc.toVector())
    }

    fun isItemAt(location: Location): Boolean {
        return this.droppedAt.contains(location.toVector())
    }

    fun pickedUpAt(location: Location) {
        this.droppedAt.remove(location.toVector())
    }

    fun removeItem(id: UUID) {
        this.items.remove(id)
    }
}