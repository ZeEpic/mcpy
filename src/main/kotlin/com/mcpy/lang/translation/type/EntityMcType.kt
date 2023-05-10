package com.mcpy.lang.translation.type

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.Context
import org.bukkit.entity.Entity

class EntityMcType : McPyType("entity", Type(Entity::class)) {

    override fun translateInput(expression: GenericExpression, firstToken: Token, context: Context): String {
        val resultType = super.translateInput(expression, firstToken, context)
        return "(${resultType} instanceof Entity ? (Entity) ${resultType} : null)"
    }

    override fun generateCommandParser(argument: Int, argName: Name): String {
        return "not implemented"
    }
}