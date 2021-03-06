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
package network.cow.mc.minigame.smash

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.util.ComponentMessageThrowable
import network.cow.minigame.noma.api.CountdownTimer
import network.cow.minigame.noma.api.Game
import network.cow.minigame.noma.api.config.PhaseConfig
import network.cow.minigame.noma.spigot.SpigotCountdownTimer
import network.cow.minigame.noma.spigot.SpigotGame
import network.cow.minigame.noma.spigot.phase.SpigotPhase
import network.cow.minigame.noma.spigot.phase.VotePhase
import network.cow.minigame.noma.spigot.pool.WorldMeta
import network.cow.minigame.smash.command.SetDamageCommand
import network.cow.minigame.smash.command.SoundCommand
import network.cow.minigame.smash.command.UnstuckCommand
import network.cow.minigame.smash.config.Config
import network.cow.minigame.smash.config.MapConfig
import network.cow.minigame.smash.event.PlayerLostLifeEvent
import network.cow.minigame.smash.item.ItemManger
import network.cow.minigame.smash.event.ItemRemoveEvent
import network.cow.minigame.smash.item.ItemSpawner
import network.cow.minigame.smash.item.ItemType
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemDespawnEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionEffectTypeWrapper
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.RenderType
import org.bukkit.util.Vector
import kotlin.math.pow
import kotlin.math.roundToInt


class SmashGame(game: Game<Player>, config: PhaseConfig<Player>) : SpigotPhase(game, config) {

    private lateinit var itemManager: ItemManger
    private lateinit var gameConfig: Config
    private lateinit var mapConfig: MapConfig

    override fun onPlayerJoin(player: Player) = Unit
    override fun onPlayerLeave(player: Player) = Unit
    override fun onTimeout() = Unit

    // TODO: track stats (kills etc.)
    // TODO: use itemBuilder
    // TODO: winning phase

    override fun onStart() {
        val worldMeta: WorldMeta = this.game.store.get("map") ?: error("no WorldMeta found")
        val plugin = JavaPlugin.getPlugin(SmashPlugin::class.java)
        mapConfig = MapConfig.from((this.game as SpigotGame).world, worldMeta)
        gameConfig = Config.fromMap(this.game.config.options)
        itemManager = ItemManger(gameConfig)

        plugin.getCommand("unstuck")?.setExecutor(UnstuckCommand(mapConfig.playerSpawnLocations))
        plugin.getCommand("setdamage")?.setExecutor(SetDamageCommand())

        // DEBUG ONLY
        plugin.getCommand("playsound")?.setExecutor(SoundCommand())
        plugin.getCommand("playsound")?.setTabCompleter { commandSender, command, s, strings ->
            Sound.values().map { it.toString() }.filter { it.contains(strings[0]) }.toList()
        }

        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val damageObj = scoreboard.registerNewObjective("damage", "dummy", Component.text("%").color(NamedTextColor.LIGHT_PURPLE), RenderType.INTEGER)
        val lifeObj = scoreboard.registerNewObjective("lives", "dummy", Component.empty(), RenderType.INTEGER)
        damageObj.displaySlot = DisplaySlot.BELOW_NAME
        lifeObj.displaySlot = DisplaySlot.PLAYER_LIST

        ItemSpawner(
            gameConfig.itemsPerInterval,
            gameConfig.items,
            mapConfig.itemSpawnLocations,
            gameConfig.items.map { it.type }.toList(), // generate items to use from configured items
            gameConfig.maxConcurrentItems,
            itemManager
        ).runTaskTimer(
            plugin,
            gameConfig.itemSpawnerDelay.toLong(),
            gameConfig.itemSpawnerInterval.toLong()
        )

        // set basic values
        this.game.getIngamePlayers().forEach {
            val attr = it.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            if (gameConfig.livesPerPlayer < 0) { // elimination is not enabled -> unlimited lives
                // display hearts in a special way to allow the player to distinguish if
                // elimination is enabled and if it is not
                attr?.baseValue = 6.0
                it.absorptionAmount = 6.0
            } else {
                attr?.baseValue = gameConfig.livesPerPlayer.toDouble() * 2
            }
            it.allowFlight = true
            it.isInvulnerable = false
            it.gameMode = GameMode.ADVENTURE
            it.scoreboard = scoreboard
            it.setLivesLeft(gameConfig.livesPerPlayer)
            println(it.getLivesLeft())
        }

        SpigotCountdownTimer(5, "")
            .onTick { time ->
                this.game.getIngamePlayers().forEach {
                    if (time == 0L) return@onTick
                    it.sendTitle("", "??d??l$time", 10, 20, 10)
                    it.walkSpeed = 0.0f
                    it.addPotionEffect(PotionEffect(PotionEffectType.JUMP, Int.MAX_VALUE, 200, false, false))
                    it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_PLING, .5f, .5f)
                }
            }
            .onDone {
                this.game.getIngamePlayers().forEach {
                    it.resetTitle()
                    it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_PLING, .5f, 1f)
                    it.removePotionEffect(PotionEffectType.JUMP)
                    it.walkSpeed = 0.2f
                }
            }
            .silent()
            .start()


        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            this.game.getIngamePlayers().forEach {
                val name = (it.displayName() as TextComponent).content()

                val damageScore = damageObj.getScore(name)
                damageScore.score = it.getDamagePercentage()

                val lifeScore = lifeObj.getScore(name)
                lifeScore.score = it.getLivesLeft()

                val text = Component.text("Damage: ").color(NamedTextColor.GRAY)
                    .append(
                        it.damageToComponent()
                    )
                    .append(
                        Component.text(" | ").color(NamedTextColor.GRAY)
                    )
                    .append(
                        Component.text("Kills: ").color(NamedTextColor.GRAY)
                    )
                    .append(
                        Component.text("${it.getEliminations()}").color(NamedTextColor.LIGHT_PURPLE)
                    )
                it.sendActionBar(text)
            }
        }, 0, 20)
    }

    override fun onStop() {
        this.storeMiddleware.store("winners", null)
    }

    @EventHandler
    private fun onItemRemove(event: ItemRemoveEvent) {
        this.itemManager.removeItem(event.item.id)
    }

    @EventHandler
    private fun onPlayerToggleFlight(event: PlayerToggleFlightEvent) {
        val player = event.player
        if (player.walkSpeed == 0f) { // player is frozen, any movement should not be possible
            event.isCancelled = true
            return
        }
        player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_FLAP, .5f, 1.0f)
        player.velocity = player.location.direction.setY(0.5).normalize().multiply(2)
        player.allowFlight = false
        event.isCancelled = true
    }

    @EventHandler
    private fun onPlayerLostLife(event: PlayerLostLifeEvent) {
        val livesLeft = event.player.getLivesLeft()

        // player will not be affected by any attacks for 2 seconds after respawn
        // this is the same period as in nintendo smash
        event.player.isInvulnerable = true
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(SmashPlugin::class.java), Runnable {
            event.player.isInvulnerable = false
        }, 20 * 2)

        event.player.getHitter()?.player?.addElimination()

        if (livesLeft < 0) { // we have infinite lives left
            event.player.teleport(mapConfig.playerSpawnLocations.random())
            return
        }

        if (livesLeft == 0) {
            event.player.gameMode = GameMode.SPECTATOR
            event.player.sendMessage(Component.text("DU BIST RAUS!!!").color(NamedTextColor.BLUE))
            event.player.playSound(event.player.location, Sound.ENTITY_ENDER_DRAGON_GROWL, .5f, 1.0f)
        }
        event.player.teleport(mapConfig.playerSpawnLocations.random())
    }

    @EventHandler
    private fun onPickUp(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        val id = event.item.itemStack.getSmashId() ?: return
        val item = this.itemManager.getItemById(id) ?: return
        val dropLocation = item.itemStack().getDropLocation() ?: return

        // you can only have one item at a time in the inventory
        if (!(event.entity as Player).inventory.isEmpty) {
            event.isCancelled = true
            return
        }

        this.itemManager.pickedUpAt(dropLocation)
        item.onPickUp(event.entity as Player)
        item.register()
    }

    @EventHandler
    private fun onPlayerOutOfWorld(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (event.cause != EntityDamageEvent.DamageCause.VOID) return
        player.looseLife()
    }

    @EventHandler
    private fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.damager !is Player || event.entity !is Player) return
        val damager = event.damager as Player
        val damaged = event.entity as Player
        if (damager.inventory.itemInMainHand.type != Material.AIR) return
        damaged.setHitter(network.cow.mc.minigame.smash.Hitter(damager, ItemType.NONE))
        if (damager.passengers.contains(damaged)) {
            damager.eject()
        }
        damaged.knockback(damager.location.direction, gameConfig.baseKnockback)
    }

    @EventHandler
    private fun onPlayerInteract(event: PlayerInteractEntityEvent) {
        if (event.rightClicked !is Player) return
        val player = event.player

        if (!player.canPickUpPlayer()) return

        // Player pickup can only happen if there is no item equipped
        if (event.player.inventory.itemInMainHand.type != Material.AIR) return

        val clicked = event.rightClicked as Player
        player.addPassenger(clicked)
        player.setCanPickUpPlayer(false)

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(SmashPlugin::class.java), Runnable {
            player.setCanPickUpPlayer(true)
        }, this.gameConfig.playerPickUpCooldown.toLong() * 20)
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player

        // reset double jump if player is on ground again
        if (player.location.block.getRelative(BlockFace.DOWN).type != Material.AIR) {
            player.allowFlight = true
        }

        val vel = e.to.clone().toVector().subtract(e.from.clone().toVector())
        if (vel.lengthSquared() < 1) {
            val prev = player.getSmashState(StateKey.VELOCITY, "LOW")
            if (prev == "HIGH" && player.canDestroySurroundings()) {
                destroyAndReplaceBlockByBlock(player)
                player.setDestroySurroundings(false)
            }
            player.setSmashState(StateKey.VELOCITY, "LOW")
        } else if (vel.lengthSquared() >= 1) {
            player.setSmashState(StateKey.VELOCITY, "HIGH")
        }
    }
}