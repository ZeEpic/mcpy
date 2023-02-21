package com.mcpy.lang.abstractions

import kotlin.reflect.KClass

class Type(val type: String) {

    constructor(clazz: KClass<*>) : this(clazz.qualifiedName!!)

    companion object {

        val VOID = Type("void")
        val STRING = Type("str")
        val NUMBER = Type("num")
        val BOOLEAN = Type("bool")

        fun toJava(type: String): Name? {
            if (type.startsWith("list[") && type.endsWith("]")) {
                return Name("java.util.List<" + (toJava(type.substring(5, type.length - 1)) ?: return null) + ">", Name.NameType.CLASS_NAME
                )
            }

            return Name(when (type) {
                "str" -> "String"
                "num" -> "double"
                "bool" -> "boolean"
                "void" -> "void"
                "player" -> "Player"
                else -> return null
            }, Name.NameType.CLASS_NAME
            )

        }
    }
}