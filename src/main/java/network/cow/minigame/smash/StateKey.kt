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
    ITEM_DROP_LOCATION("itemDropLocation"),
    CAN_USE_UNSTUCK_COMMAND("canUseUnstuckCommand"),
    HITTER("hitter"),
    JET_PACK_USES("jetPackUses"),
    CAN_PICK_UP_PLAYER("canPickUpPlayer"),
    CAN_DESTROY_SURROUNDINGS("canDestroySurroundings")
}