package network.cow.minigame.smash

import network.cow.minigame.smash.item.ItemType
import org.bukkit.entity.Player

data class Hitter(val player: Player, val itemType: ItemType)