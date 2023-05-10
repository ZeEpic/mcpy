package com.mcpy.lang.translation.type

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.classNameFromQualifiedName
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.Context
import java.lang.IllegalArgumentException

abstract class McPyType(val mcpyName: String, val javaType: Type) {

    // This is when a function is called of the type such as player("ZeEpic") or str(a_list)
    open fun translateInput(expression: GenericExpression, firstToken: Token, context: Context): String {
        val resultType = expression.resultType?.type?.classNameFromQualifiedName()
        require(resultType != null && resultType != "void" && expression.tokens.isNotEmpty(), firstToken) {
            "Can't convert this expression to a $mcpyName"
        }
        return resultType
    }

    // For when the argument of this type is used as a part of a command definition
    // Not all types are supported
    abstract fun generateCommandParser(argument: Int, argName: Name): String

    fun commandTryCatchBlock(tryBlock: String, message: String, exception: Type = Type(IllegalArgumentException::class)): String {
        return "try {\n" +
                "    $tryBlock\n" +
                "} catch (${exception.type} exception) {\n" +
                "    sendMessage(sender, $message);\n" +
                "    return;\n" +
                "}"
    }

    companion object {
        val types = listOf(
            StringType(),
            MaterialType()
        )
    }

}