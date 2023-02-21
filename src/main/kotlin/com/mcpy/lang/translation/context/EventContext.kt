package com.mcpy.lang.translation.context

import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.translation.identifier.Identifier

class EventContext(val event: Type, identifiers: MutableList<Identifier>) : Context(identifiers)
