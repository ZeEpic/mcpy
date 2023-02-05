package com.rimlang.rim.translation.context

import com.rimlang.rim.translation.identifier.Identifier
import com.rimlang.rim.abstractions.Name

class EventContext(val event: Name, identifiers: MutableList<Identifier>) : Context(identifiers)
