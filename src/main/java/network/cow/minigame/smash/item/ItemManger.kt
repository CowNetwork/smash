package network.cow.minigame.smash.item

import network.cow.minigame.smash.config.Config
import network.cow.minigame.smash.config.ItemConfig
import java.lang.IllegalArgumentException
import java.util.*

class ItemManger(private val config: Config) {
    private val items: MutableMap<UUID, Item> = mutableMapOf()

    fun createItem(type: ItemType, itemConfig: ItemConfig): Item {
       return when (type) {
            ItemType.HAMMER -> {
                val item = Hammer(itemConfig.data["knockbackStrengthPercentage"] as Double, config.baseKnockback)
                this.items[item.id] = item
                item
            }
           else -> throw IllegalArgumentException("lol")
        }
    }

    fun getItemById(id: UUID): Item? {
        return this.items[id]
    }

    fun removeItem(id: UUID) {
        this.items.remove(id)
    }
}