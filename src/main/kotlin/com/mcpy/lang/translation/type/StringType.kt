package com.mcpy.lang.translation.type

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.Context

class StringType : McPyType("str", Type("String")) {
    override fun translateInput(expression: GenericExpression, firstToken: Token, context: Context): String {
        val resultType = super.translateInput(expression, firstToken, context)
        when (resultType) {
            javaType.type -> {
                return expression.translate(context)
            }
            "List<Double>" -> {
                return "\"[\" + ${expression.translate(context)}.stream().map((it) -> Double.parseDouble(it)).collect(Collectors.joining(\", \")) + \"]\""
            }
            "List<Boolean>" -> {
                return "\"[\" + ${expression.translate(context)}.stream().map((it) -> Boolean.parseBoolean(it)).collect(Collectors.joining(\", \")) + \"]\""
            }
            "List<String>" -> {
                return "\"[\" + String.join(\", \", ${expression.translate(context)}) + \"]\""
            }
        }
        if (resultType.startsWith("List<")) {
            return "\"[\" + ${expression.translate(context)}.stream().map((it) -> it.toString()).collect(Collectors.joining(\", \")) + \"]\""
        }
        return "\"\" + ${expression.translate(context)}"
    }

    override fun generateCommandParser(argument: Int, argName: Name): String {
        return "String ${argName.converted.value} = args[$argument];"
    }
}