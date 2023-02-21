package com.mcpy.lang.translation

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.asResource

object Events {

    private val eventMap = HashMap<Name, Type>()

    init {
        val eventMapString = "event_map.txt".asResource()

        // Create event map
        eventMapString.split("\n")
            .map { it.split(": ") }
            .filter { it.size == 2 }
            .forEach { (clazz, dotEvent) ->
                eventMap[Name(dotEvent.trim(), Name.NameType.EVENT)] = Name(clazz, Name.NameType.CLASS).toType()
            }
    }

    fun spigotEvent(event: Name): String? {
        if (event.value == "server.start") {
            return "onEnable"
        }
        if (event.value == "server.stop") {
            return "onDisable"
        }
        val spigotEvent = eventMap[event] ?: return null
        return spigotEvent.type
    }

    private fun Int.toStringWithoutZero()
            = if (this == 0) ""
              else this.toString()

}