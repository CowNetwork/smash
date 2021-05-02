package network.cow.minigame.smash

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class BummsTask(private val player: Player, speed: Vector) : BukkitRunnable() {

    companion object {
        const val DECELERATION_RATE = 0.98
        const val GRAVITY_CONSTANT = 0.08
        const val VANILA_ANTICHEAT_THRESHOLD = 9.5 // actual 10D
    }

    private var velY = speed.y
    private var velX = speed.x
    private var velZ = speed.z

    override fun run() {
        if (velY > VANILA_ANTICHEAT_THRESHOLD) {
            player.velocity = Vector(velX, VANILA_ANTICHEAT_THRESHOLD, velZ);
        } else {
            player.velocity = Vector(velX, velY, velZ)
            this.cancel();
        }
        velY -= GRAVITY_CONSTANT;
        velY *= DECELERATION_RATE;

        velX *= DECELERATION_RATE;
        velZ *= DECELERATION_RATE;
    }
}