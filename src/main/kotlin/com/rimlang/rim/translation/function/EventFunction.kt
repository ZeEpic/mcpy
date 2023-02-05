package com.rimlang.rim.translation.function

import com.rimlang.rim.translation.identifier.Function
import com.rimlang.rim.abstractions.Name
import com.rimlang.rim.abstractions.Type

class EventFunction(override val name: Name, eventType: Type, override var body: String) : Function(
    mutableListOf("EventHandler"),
    Type("void"),
    name,
    "$eventType event",
    body
)