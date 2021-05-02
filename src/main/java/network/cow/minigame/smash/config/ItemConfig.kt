package network.cow.minigame.smash.config

import network.cow.minigame.smash.item.ItemType
import org.bukkit.configuration.MemorySection

/*
  - type: HAMMER
    data:
      knockbackStrengthPercentage: 5
 */

data class ItemConfig(val type: ItemType, val data: Map<String, Any>)
