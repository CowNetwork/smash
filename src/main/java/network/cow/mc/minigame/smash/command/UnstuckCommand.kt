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
package network.cow.mc.minigame.smash.command

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