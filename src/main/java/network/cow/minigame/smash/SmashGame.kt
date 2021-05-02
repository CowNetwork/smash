package network.cow.minigame.smash

import net.kyori.adventure.text.Component
import network.cow.minigame.noma.api.Game
import network.cow.minigame.noma.api.config.PhaseConfig
import network.cow.minigame.noma.api.phase.EmptyPhaseResult
import network.cow.minigame.noma.spigot.SpigotGame
import network.cow.minigame.noma.spigot.phase.SpigotPhase
import network.cow.minigame.noma.spigot.phase.VotePhase
import network.cow.minigame.noma.spigot.pool.WorldMeta
import network.cow.minigame.smash.config.Config
import network.cow.minigame.smash.config.MapConfig
import network.cow.minigame.smash.item.ItemManger
import network.cow.minigame.smash.item.ItemRemoveEvent
import network.cow.minigame.smash.item.ItemSpawner
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.configuration.MemorySection
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.pow


class SmashGame(game: Game<Player>, config: PhaseConfig<Player>) : SpigotPhase<EmptyPhaseResult>(game, config) {

    private lateinit var itemManager: ItemManger

    override fun onPlayerJoin(player: Player) = Unit
    override fun onPlayerLeave(player: Player) = Unit

    override fun onStart() {
        val worldMeta = (this.game.getPhase("vote") as VotePhase<WorldMeta>).firstVotedItem()
        val mapConfig = MapConfig.from((this.game as SpigotGame).world, worldMeta)
        val conf = Config.fromMap(this.game.config.options)
        itemManager = ItemManger(conf)

        ItemSpawner(
            conf.itemsPerInterval,
            conf.items,
            mapConfig.itemSpawnLocations,
            conf.items.map { it.type }.toList(), // generate items to use from configured items
            itemManager
        ).runTaskTimer(
            JavaPlugin.getPlugin(SmashPlugin::class.java),
            conf.itemSpawnerDelay.toLong(),
            conf.itemSpawnerInterval.toLong()
        )

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
    private fun onPickUp(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        val id = event.item.itemStack.getSmashId() ?: return
        val item = this.itemManager.getItemById(id) ?: return
        item.register()
    }

    @EventHandler
    private fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        /* if (e.entity !is Player) {
             return
         }

         val player = e.entity as Player
         val vel = e.damager.location.clone().direction.normalize().multiply(0)

         // bumms the player away
         BummsTask(player, vel).runTaskTimer(JavaPlugin.getPlugin(SmashPlugin::class.java), 0, 1)*/
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