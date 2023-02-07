package com.mcpy.lang.translation.context

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.translation.identifier.Identifier

class EventContext(val event: Name, identifiers: MutableList<Identifier>) : Context(identifiers)
