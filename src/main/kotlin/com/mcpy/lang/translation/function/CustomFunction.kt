package com.mcpy.lang.translation.function

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.translation.identifier.Function

class CustomFunction(
    override val annotations: MutableList<String>,
    override val returnType: Type,
    override val name: Name,
    override val parameters: String,
    override var body: String
) : Function(annotations, returnType, name, parameters, body)