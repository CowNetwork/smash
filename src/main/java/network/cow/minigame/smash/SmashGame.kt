package network.cow.minigame.smash

import network.cow.minigame.noma.api.Game
import network.cow.minigame.noma.api.config.PhaseConfig
import network.cow.minigame.noma.api.phase.EmptyPhaseResult
import network.cow.minigame.noma.spigot.phase.SpigotPhase
import network.cow.minigame.smash.config.Config
import network.cow.minigame.smash.item.ItemManger
import network.cow.minigame.smash.item.ItemRemoveEvent
import network.cow.minigame.smash.item.ItemSpawner
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.pow


class SmashGame(game: Game<Player>, config: PhaseConfig<Player>) : SpigotPhase<EmptyPhaseResult>(game, config) {

    private lateinit var itemManager: ItemManger

    override fun onPlayerJoin(player: Player) = Unit
    override fun onPlayerLeave(player: Player) = Unit

    override fun onStart() {
        val conf: Config = Config.fromMap(this.game.config.options)

        ItemSpawner(
            conf.itemsPerInterval,
            conf.items,
            conf.itemSpawnLocations, // locations
            conf.items.map { it.type }.toList(), // generate items to use from configured items
            itemManager
        ).runTaskTimer(
            JavaPlugin.getPlugin(SmashPlugin::class.java),
            conf.itemSpawnerDelay.toLong(),
            conf.itemSpawnerInterval.toLong()
        )
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
        if (e.entity !is Player) {
            return
        }

        val player = e.entity as Player
        val vel = e.damager.location.clone().direction.normalize().multiply(0)

        // bumms the player away
        BummsTask(player, vel).runTaskTimer(JavaPlugin.getPlugin(SmashPlugin::class.java), 0, 1)
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
        val vel = e.to.clone().toVector().subtract(e.from.clone().toVector())

        if (vel.lengthSquared() < 1) {
            val prev = player.getSmashState(StateKey.VELOCITY, "LOW")
            if (prev == "HIGH") {
                println("DESTROY")
                getNearbyBlocks(player.getLocation(), 3).forEach {
                    val falling = it.world.spawnFallingBlock(it.location, it.blockData)
                    falling.setHurtEntities(false)
                    falling.dropItem = false
                    it.type = Material.AIR
                }
                player.world.playSound(player.location, Sound.ENTITY_WITHER_BREAK_BLOCK, 1.0f, 1.0f)
            }

            player.setSmashState(StateKey.VELOCITY, "LOW")
        } else if (vel.lengthSquared() >= 1) {
            player.setSmashState(StateKey.VELOCITY, "HIGH")
        }
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
                        blocks.add(block)
                    }
                }
            }
        }
        return blocks
    }
}