package network.cow.minigame.smash.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import network.cow.minigame.noma.spigot.pool.SpawnLocation
import network.cow.minigame.smash.canUseUnstuckCommand
import network.cow.minigame.smash.setCanUseUnstuckCommand
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UnstuckCommand(private val spawnLocations: List<Location>) : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (!sender.canUseUnstuckCommand()) {
            sender.sendMessage(Component.text("CANT USE UNSTUCK COMMAND").color(NamedTextColor.RED))
            return false
        }
        sender.setCanUseUnstuckCommand(false)
        sender.teleport(spawnLocations.random())
        return true
    }
}