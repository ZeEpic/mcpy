package com.rimlang.rim.translation.function

import com.rimlang.rim.translation.identifier.Function
import com.rimlang.rim.abstractions.Name
import com.rimlang.rim.abstractions.Type

class CustomFunction(
    override val annotations: MutableList<String>,
    override val returnType: Type,
    override val name: Name,
    override val parameters: String,
    override var body: String
) : Function(annotations, returnType, name, parameters, body)