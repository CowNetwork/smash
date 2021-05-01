package network.cow.minigame.smash.config

import network.cow.minigame.smash.item.ItemType

class Config(
    val baseKnockback: Double, // base knockback
    val itemsPerInterval: Int, // how many items should be dropped per interval
    val items: List<ItemConfigEntry> // config for all items
) {

}