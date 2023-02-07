package com.mcpy.lang.translation.context

import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.translation.identifier.Identifier

class FunctionContext(val returnType: Type, identifiers: MutableList<Identifier>) : Context(identifiers)