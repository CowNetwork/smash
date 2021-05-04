package network.cow.minigame.smash.command

import net.kyori.adventure.text.Component
import network.cow.minigame.smash.setDamage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetDamageCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (!sender.hasPermission("network.cow.minigame.smash.can-set-damage")) return false

        if (args.size < 2) {
            sender.sendMessage(Component.text("usage: /setdamage <player-name> <damage>"))
            return false
        }

        val playerName = args[0]
        val damage = args[1].toDoubleOrNull()

        val acutalPlayer = Bukkit.getPlayer(playerName)
        if (acutalPlayer == null) {
            sender.sendMessage(Component.text("could not find player with that name"))
            return false
        }

        if (damage == null) {
            sender.sendMessage(Component.text("not a valid double value"))
            return false
        }

        acutalPlayer.setDamage(damage)
        return true
    }
}