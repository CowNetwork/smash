package network.cow.minigame.smash

enum class StateKey(val key: String) {

    // how much knockback the player will receive
    DAMAGE("damage"),

    // the knockback reduction in percent
    KNOCKBACK_REDUCTION("knockbackReduction"),

    // how many lives the player has left until he is removed fromt the game
    LIVES("lives"),

    // how many players the player eliminated
    KILLS("kills"),

    VELOCITY("velocity"),

    ITEM_ID("itemId"),

    CAN_USE_UNSTUCK_COMMAND("canUseUnstuckCommand"),

    HITTER("hitter"),

    JET_PACK_USES("jetPackUses"),

    CAN_PICK_UP_PLAYER("canPickUpPlayer")
}