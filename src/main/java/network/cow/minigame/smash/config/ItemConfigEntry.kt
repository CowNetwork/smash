package network.cow.minigame.smash.config

import network.cow.minigame.smash.item.ItemType

/*
  - type: HAMMER
    data:
      knockbackStrengthPercentage: 5
 */

data class ItemConfigEntry(val type: ItemType, val data: Map<String, Any>)
