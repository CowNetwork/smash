package network.cow.minigame.smash

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import network.cow.minigame.smash.event.PlayerLostLifeEvent
import network.cow.spigot.extensions.state.clearState
import network.cow.spigot.extensions.state.getState
import network.cow.spigot.extensions.state.setState
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt


fun Player.knockback(direction: Vector, power: Double) {
    if (this.isInvulnerable) return
    val actualKnockback = this.getDamage() * (1.0 - this.getKnockbackReduction()) + power
    this.setDamage(this.getDamage() + power)
    this.setDestroySurroundings(true)
    BummsTask(
        this,
        // use actualKnockback instead of this.getKnockbackStrength() here,
        // because we also want to account the temporary reduction if there is any
        direction.normalize().multiply(actualKnockback)
    ).runTaskTimer(JavaPlugin.getPlugin(SmashPlugin::class.java), 0, 1)
}

fun Player.damageToComponent(): Component {
    val percentage = this.getDamagePercentage()
    val comp = Component.text("$percentage%")

    if (percentage in 0..20) {
        return comp.color(NamedTextColor.GREEN)
    }

    if (percentage in 21..50) {
        return comp.color(NamedTextColor.YELLOW)
    }

    if (percentage in 51..100) {
        return comp.color(NamedTextColor.RED)
    }

    return comp.color(NamedTextColor.DARK_RED)
}

fun Player.getDamagePercentage(): Int {
    return ((this.getDamage() * 0.8) * 100).roundToInt()
}

fun Player.looseLife() {
    val livesLeft = this.getSmashState(StateKey.LIVES, 1).dec()
    this.setDamage(0.0)

    if (livesLeft < 0) { // unlimited lives
        Bukkit.getPluginManager().callEvent(PlayerLostLifeEvent(this))
        return
    }

    this.inventory.clear()
    this.setSmashState(StateKey.LIVES, livesLeft)
    this.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = livesLeft.toDouble() * 2
    Bukkit.getPluginManager().callEvent(PlayerLostLifeEvent(this))
}

fun Player.canDestroySurroundings(): Boolean {
    return this.getSmashState(StateKey.CAN_DESTROY_SURROUNDINGS, false)
}

fun Player.setDestroySurroundings(can: Boolean) {
    this.setSmashState(StateKey.CAN_DESTROY_SURROUNDINGS, can)
}

fun Player.getEliminations(): Int {
    return this.getSmashState(StateKey.KILLS, 0)
}

fun Player.addElimination() {
    this.setSmashState(StateKey.KILLS, this.getSmashState(StateKey.KILLS, 0).inc())
}

fun Player.canPickUpPlayer(): Boolean {
    return this.getSmashState(StateKey.CAN_PICK_UP_PLAYER, true)
}

fun Player.setCanPickUpPlayer(can: Boolean) {
    return this.setSmashState(StateKey.CAN_PICK_UP_PLAYER, can)
}

fun Player.getJetPackUses(): Int {
    return this.getSmashState(StateKey.JET_PACK_USES, 0)
}

fun Player.setJetPackUses(uses: Int) {
    return this.setSmashState(StateKey.JET_PACK_USES, uses)
}

fun Player.getHitter(): Hitter? {
    return this.getSmashState(StateKey.HITTER)
}

fun Player.setHitter(hitter: Hitter?) {
    if (hitter == null) {
        this.clearState(SmashPlugin::class.java, StateKey.HITTER.key)
        return
    }
    this.setSmashState(StateKey.HITTER, hitter)
}

fun Player.getLivesLeft(): Int {
    return this.getSmashState(StateKey.LIVES, 1)
}

fun Player.setLivesLeft(lives: Int) {
    this.setSmashState(StateKey.LIVES, lives)
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

fun Player.removeDamagePercentage(percent: Double) {
    val remaining = this.getDamage() * (1.0 - percent)
    this.setDamage(remaining)
}

fun Player.setDamage(damage: Double) {
    return this.setSmashState(StateKey.DAMAGE, damage)
}

fun Player.getDamage(): Double {
    return this.getSmashState(StateKey.DAMAGE, 0.0)
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