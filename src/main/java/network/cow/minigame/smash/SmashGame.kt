package network.cow.minigame.smash

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import network.cow.minigame.noma.api.Game
import network.cow.minigame.noma.api.config.PhaseConfig
import network.cow.minigame.noma.api.phase.EmptyPhaseResult
import network.cow.minigame.noma.spigot.SpigotGame
import network.cow.minigame.noma.spigot.phase.SpigotPhase
import network.cow.minigame.noma.spigot.phase.VotePhase
import network.cow.minigame.noma.spigot.pool.WorldMeta
import network.cow.minigame.smash.command.UnstuckCommand
import network.cow.minigame.smash.config.Config
import network.cow.minigame.smash.config.MapConfig
import network.cow.minigame.smash.event.PlayerLostLifeEvent
import network.cow.minigame.smash.item.ItemManger
import network.cow.minigame.smash.event.ItemRemoveEvent
import network.cow.minigame.smash.item.ItemSpawner
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.TimeUnit
import kotlin.math.pow


class SmashGame(game: Game<Player>, config: PhaseConfig<Player>) : SpigotPhase<EmptyPhaseResult>(game, config) {

    private lateinit var itemManager: ItemManger
    private lateinit var gameConfig: Config
    private lateinit var mapConfig: MapConfig

    override fun onPlayerJoin(player: Player) = Unit
    override fun onPlayerLeave(player: Player) = Unit

    // TODO: more items
    // TODO: determine percentage based on knockbackStrength and display
    // TODO: ROCKET LAUNCHER
    // TODO: JET_PACK

    override fun onStart() {
        val worldMeta = (this.game.getPhase("vote") as VotePhase<WorldMeta>).firstVotedItem()
        mapConfig = MapConfig.from((this.game as SpigotGame).world, worldMeta)
        gameConfig = Config.fromMap(this.game.config.options)
        itemManager = ItemManger(gameConfig)

        JavaPlugin.getPlugin(SmashPlugin::class.java).
            getCommand("unstuck")?.
            setExecutor(UnstuckCommand(mapConfig.playerSpawnLocations))

        ItemSpawner(
            gameConfig.itemsPerInterval,
            gameConfig.items,
            mapConfig.itemSpawnLocations,
            gameConfig.items.map { it.type }.toList(), // generate items to use from configured items
            itemManager
        ).runTaskTimer(
            JavaPlugin.getPlugin(SmashPlugin::class.java),
            gameConfig.itemSpawnerDelay.toLong(),
            gameConfig.itemSpawnerInterval.toLong()
        )

        // set basic values
        this.game.getPlayers().forEach {
            val attr = it.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            if (gameConfig.livesPerPlayer < 0) { // elimination is not enabled -> unlimited lives
                // display hearts in a special way to allow the player to distinguish if
                // elimination is enabled and if it is not
                attr?.baseValue = 6.0
                it.absorptionAmount = 6.0
                return
            }
            attr?.baseValue = gameConfig.livesPerPlayer.toDouble() * 2
            it.allowFlight = true
            it.setLivesLeft(gameConfig.livesPerPlayer)
            it.setCanDoubleJump(true)
        }

        Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(SmashPlugin::class.java), Runnable {
            this.game.getPlayers().forEach {
                it.sendActionBar(Component.text(it.getKnockbackStrength()))
            }
        }, 0, 20)

    }

    override fun onStop(): EmptyPhaseResult {
        return EmptyPhaseResult()
    }

    override fun onTimeout() = Unit

    @EventHandler
    private fun onItemRemove(event: ItemRemoveEvent) {
        this.itemManager.removeItem(event.item.id)
    }

    @EventHandler
    private fun onPlayerToggleFlight(event: PlayerToggleFlightEvent) {
        val player = event.player

        if (!player.canDoubleJump()) {
            player.sendMessage(Component.text("NEIN").color(NamedTextColor.RED))
            event.isCancelled = true
            return
        }

        player.velocity = player.location.direction.setY(1.0).normalize().multiply(1.5)
        player.allowFlight = false
        player.setCanDoubleJump(false)
        event.isCancelled = true

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(SmashPlugin::class.java), Runnable {
            player.setCanDoubleJump(true)
            player.allowFlight = true
            // TODO: sound
        }, 20 * gameConfig.doubleJumpCooldown.toLong())
    }

    @EventHandler
    private fun onPlayerLostLife(event: PlayerLostLifeEvent) {
        val livesLeft = event.player.getLivesLeft()
        if (livesLeft < 0) return // we have infinite lives left
        if (livesLeft == 0) {
            event.player.gameMode = GameMode.SPECTATOR
            event.player.sendMessage(Component.text("DU BIST RAUS!!!").color(NamedTextColor.BLUE))
            // TODO: play sound
        }
        event.player.teleport(mapConfig.playerSpawnLocations.random())
    }

    @EventHandler
    private fun onPickUp(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        val id = event.item.itemStack.getSmashId() ?: return
        val item = this.itemManager.getItemById(id) ?: return
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
        println((event.damager as Player).inventory.itemInMainHand.type)
        val damager = event.damager as Player
        val damaged = event.entity as Player
        if (damager.inventory.itemInMainHand.type != Material.AIR) return
        damaged.knockback(damager.location.direction, gameConfig.baseKnockback)
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
        val vel = e.to.clone().toVector().subtract(e.from.clone().toVector())
        if (vel.lengthSquared() < 1) {
            val prev = player.getSmashState(StateKey.VELOCITY, "LOW")
            if (prev == "HIGH") {
                destroyAndReplaceBlockByBlock(player)
            }
            player.setSmashState(StateKey.VELOCITY, "LOW")
        } else if (vel.lengthSquared() >= 1) {
            player.setSmashState(StateKey.VELOCITY, "HIGH")
        }
    }

    private fun destroyAndReplaceBlockByBlock(player: Player) {
        val prev = mutableListOf<BlockState>()
        val blocks = getNearbyBlocks(player.location, 3)

        // can be empty since getNearbyBlocks can be triggered if only air surrounds the player
        if (blocks.isEmpty()) return

        getNearbyBlocks(player.location, 3).forEach {
            prev.add(BlockState(it.location, it.type, it.blockData))
            val falling = it.world.spawnFallingBlock(it.location, it.blockData)
            falling.setHurtEntities(false)
            falling.dropItem = false
            it.type = Material.AIR
        }

        player.world.playSound(player.location, Sound.ENTITY_WITHER_BREAK_BLOCK, 1.0f, 1.0f)
        RebuildTask(prev.iterator()).runTaskTimer(JavaPlugin.getPlugin(SmashPlugin::class.java), 20, 20)
    }

    private fun getNearbyBlocks(location: Location, radius: Int): List<Block> {
        val blocks = mutableListOf<Block>()
        for (x in location.blockX - radius..location.blockX + radius) {
            for (y in location.blockY - radius..location.blockY + radius) {
                for (z in location.blockZ - radius..location.blockZ + radius) {
                    val block = location.world.getBlockAt(x, y, z)
                    val dist = location.distanceSquared(block.location)
                    val probability = 1.0 - dist / radius.toDouble().pow(2)
                    if (Math.random() <= probability) {
                        if (block.type == Material.AIR) continue
                        blocks.add(block)
                    }
                }
            }
        }
        return blocks
    }
}