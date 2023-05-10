package com.mcpy.lang.translation.type

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.Context
import org.bukkit.Material

class MaterialType : McPyType("material", Type(Material::class)) {
    override fun translateInput(expression: GenericExpression, firstToken: Token, context: Context): String {
        val resultType = super.translateInput(expression, firstToken, context)
        if (resultType == javaType.type) {
            return expression.translate(context)
        }
        val string = StringType().translateInput(expression, firstToken, context)
        return "Material.valueOf($string)"
    }

    override fun generateCommandParser(argument: Int, argName: Name): String {
        return "Material ${argName.converted.value};\n" +
                commandTryCatchBlock(
                    "${argName.converted.value} = Material.valueOf(args[$argument].toUppercase());",
                    "\"&cInvalid material: \" + args[$argument] + \". Try writing a material like \"white_wool\"."
                )
    }
}