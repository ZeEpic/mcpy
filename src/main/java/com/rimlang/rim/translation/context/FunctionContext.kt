package com.rimlang.rim.translation.context

import com.rimlang.rim.translation.identifier.Identifier
import com.rimlang.rim.abstractions.Type

class FunctionContext(val returnType: Type, identifiers: MutableList<Identifier>) : Context(identifiers)