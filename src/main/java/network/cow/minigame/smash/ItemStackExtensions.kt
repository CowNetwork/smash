package network.cow.minigame.smash

import network.cow.spigot.extensions.state.getState
import network.cow.spigot.extensions.state.setState
import org.bukkit.inventory.ItemStack
import java.util.*

fun ItemStack.getSmashId(): UUID? {
    return this.getSmashState(StateKey.ITEM_ID)
}

fun ItemStack.setSmashState(key: StateKey, value: Any) {
    this.setState(SmashPlugin::class.java, key.key, value)
}

fun <T> ItemStack.getSmashState(key: StateKey): T? {
    return this.getState(SmashPlugin::class.java, key.key)
}

fun <T> ItemStack.getSmashState(key: StateKey, default: T): T {
    return this.getState(SmashPlugin::class.java, key.key, default = default!!)
}