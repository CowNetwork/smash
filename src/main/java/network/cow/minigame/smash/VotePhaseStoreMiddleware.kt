package network.cow.minigame.smash

import network.cow.minigame.noma.api.config.StoreMiddlewareConfig
import network.cow.minigame.noma.api.phase.Phase
import network.cow.minigame.noma.api.state.Store
import network.cow.minigame.noma.api.state.StoreMiddleware
import network.cow.minigame.noma.spigot.phase.VotePhase
import network.cow.minigame.noma.spigot.pool.WorldMeta

class VotePhaseStoreMiddleware(phase: Phase<*>, store: Store, config: StoreMiddlewareConfig)
    : StoreMiddleware(phase, store, config) {
    override fun transformKey(phase: Phase<*>, key: String): String {
        return key
    }

    override fun transformValue(phase: Phase<*>, value: Any?): Any? {
        val result = value as VotePhase.Result<WorldMeta>?
        return result?.items?.firstOrNull()?.value
    }
}