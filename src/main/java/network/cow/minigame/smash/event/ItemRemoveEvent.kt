package network.cow.minigame.smash.event

import network.cow.minigame.smash.item.Item
import org.bukkit.event.Event
import org.bukkit.event.HandlerList


class ItemRemoveEvent(val item: Item) : Event() {
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}