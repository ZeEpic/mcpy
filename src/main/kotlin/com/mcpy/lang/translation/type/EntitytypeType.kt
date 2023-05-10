package com.mcpy.lang.translation.type

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.Context
import org.bukkit.entity.EntityType

class EntitytypeType : McPyType("entitytype", Type(EntityType::class)) {
    override fun translateInput(expression: GenericExpression, firstToken: Token, context: Context): String {
        val resultType = super.translateInput(expression, firstToken, context)
        if (resultType == javaType.type) {
            return expression.translate(context)
        }
        val string = StringType().translateInput(expression, firstToken, context)
        return "EntityType.valueOf($string)"
    }

    override fun generateCommandParser(argument: Int, argName: Name): String {
        return "EntityType ${argName.converted.value};\n" +
                commandTryCatchBlock(
                    "${argName.converted.value} = EntityType.valueOf(args[$argument].toUppercase());",
                    "\"&cInvalid entity type: \" + args[$argument] + \". Try writing an entity type like \"elder_guardian\"."
                )
    }
}