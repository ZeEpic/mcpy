package com.mcpy.lang.translation.function

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.translation.identifier.Function

class EventFunction(override val name: Name, eventType: Type, override var body: String) : Function(
    mutableListOf("EventHandler"),
    Type.VOID,
    name,
    "$eventType event",
    body
)