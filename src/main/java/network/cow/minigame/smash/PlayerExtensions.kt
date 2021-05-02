package network.cow.minigame.smash

import network.cow.spigot.extensions.state.getState
import network.cow.spigot.extensions.state.setState
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector


fun Player.knockback(direction: Vector, power: Double) {
    val actualKnockback = this.getKnockbackStrength() * (1.0 - this.getKnockbackReduction()) + power
    this.setKnockbackStrength(this.getKnockbackStrength() + power)
    BummsTask(
        this,
        // use actualKnockback instead of this.getKnockbackStrength() here,
        // because we also want to account the temporary reduction if there is any
        direction.normalize().multiply(actualKnockback)
    ).runTaskTimer(JavaPlugin.getPlugin(SmashPlugin::class.java), 0, 1)
}

fun Player.setCanUseUnstuckCommand(can: Boolean) {
    this.setSmashState(StateKey.CAN_USE_UNSTUCK_COMMAND, can)
}

fun Player.canUseUnstuckCommand(): Boolean {
    return this.getSmashState(StateKey.CAN_USE_UNSTUCK_COMMAND, false)
}

fun Player.getKnockbackReduction(): Double {
    return this.getSmashState(StateKey.KNOCKBACK_REDUCTION, 0.0)
}

fun Player.setKnockbackStrength(strength: Double) {
    return this.setSmashState(StateKey.KNOCKBACK, strength)
}

fun Player.getKnockbackStrength(): Double {
    return this.getSmashState(StateKey.KNOCKBACK, 0.1)
}

fun Player.setSmashState(key: StateKey, value: Any) {
    this.setState(SmashPlugin::class.java, key.key, value)
}

fun <T> Player.getSmashState(key: StateKey): T? {
    return this.getState(SmashPlugin::class.java, key.key)
}

fun <T> Player.getSmashState(key: StateKey, default: T): T {
    return this.getState(SmashPlugin::class.java, key.key, default = default!!)
}