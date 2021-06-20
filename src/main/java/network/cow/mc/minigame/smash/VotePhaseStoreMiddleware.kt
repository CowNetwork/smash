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