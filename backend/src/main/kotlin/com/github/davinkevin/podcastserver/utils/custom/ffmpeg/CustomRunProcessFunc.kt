package com.github.davinkevin.podcastserver.utils.custom.ffmpeg

import arrow.core.getOrElse
import arrow.syntax.collections.firstOption
import lan.dk.podcastserver.utils.custom.ffmpeg.ProcessListener
import net.bramp.ffmpeg.RunProcessFunction
import java.io.IOException

/**
 * Created by kevin on 24/07/2016.
 */
open class CustomRunProcessFunc(private var listeners: List<ProcessListener> = listOf()) : RunProcessFunction() {

    @Throws(IOException::class)
    override fun run(args: List<String>): Process {
        val p = super.run(args)

        val toBeRemoved = listeners
                .firstOption { pl -> args.contains(pl.url) }
                .map { it.setProcess(p); it }
                .getOrElse { ProcessListener.DEFAULT_PROCESS_LISTENER }

        this.listeners = listeners - toBeRemoved

        return p
    }

    open fun add(pl: ProcessListener?): CustomRunProcessFunc {
        if (pl === null) {
            println("Is Null !")
            return this
        }
        this.listeners = listeners + pl
        return this
    }

    operator fun plus(pl: ProcessListener): CustomRunProcessFunc = this.add(pl)
}
