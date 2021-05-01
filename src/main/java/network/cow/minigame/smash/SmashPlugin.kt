package network.cow.minigame.smash

import network.cow.minigame.noma.spigot.NomaGamePlugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class SmashPlugin : NomaGamePlugin() {
    override fun onEnable() {
        super.onEnable()
        //MessagesPlugin.PREFIX = "SMASH".gradient(Gradients.MINIGAME)
    }
}