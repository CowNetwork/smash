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