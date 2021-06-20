package network.cow.mc.minigame.smash.command

import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SoundCommand : CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        val p = p0 as Player
        p.playSound(p.location, Sound.valueOf(p3[0]), (p3[1] as String).toFloat(), (p3[2] as String).toFloat())
        return false
    }

}