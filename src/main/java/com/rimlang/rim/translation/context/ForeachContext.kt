package com.rimlang.rim.translation.context

import com.rimlang.rim.translation.identifier.Identifier
import com.rimlang.rim.abstractions.Type

class ForeachContext(val loopType: Type, identifiers: MutableList<Identifier>) : Context(identifiers)