package com.github.davinkevin.podcastserver.manager.selector

import com.github.davinkevin.podcastserver.manager.worker.Type
import com.github.davinkevin.podcastserver.manager.worker.Updater
import com.github.davinkevin.podcastserver.manager.worker.noop.NoOpUpdater
import org.springframework.stereotype.Service
import java.net.URI

/**
 * Created by kevin on 06/03/15.
 */
@Service
class UpdaterSelector(val updaters: Set<Updater>) {

    fun of(url: URI): Updater = updaters.minBy { updater -> updater.compatibility(url.toASCIIString()) }!!
    fun types(): Set<Type> = updaters.map { it.type() }.toSet()

    companion object {
        @JvmStatic
        val NO_OP_UPDATER = NoOpUpdater()
    }


}
