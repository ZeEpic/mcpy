package com.rimlang.rim.abstractions

import com.rimlang.rim.camelCase
import com.rimlang.rim.pascalCase

class Name(val value: String, val type: NameType) {
    fun toType() = Type(value)

    enum class NameType(val converter: (String) -> String = {it}) {
        CLASS(String::pascalCase),
        FUNCTION(String::camelCase),
        VARIABLE(String::camelCase),
        PARAMETER(String::camelCase),
        EVENT,
        CLASS_NAME,
        ENUM_VALUE
    }
    val convertedValue: Name
        get() = Name(type.converter(value), type)
}