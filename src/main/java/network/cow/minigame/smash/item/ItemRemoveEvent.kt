package network.cow.minigame.smash.item

import org.bukkit.event.Event
import org.bukkit.event.HandlerList


class ItemRemoveEvent(val item: Item) : Event() {
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}