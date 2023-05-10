package com.mcpy.lang.syntax.chain

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.classNameFromQualifiedName
import com.mcpy.lang.clazz
import com.mcpy.lang.errors.error
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.pascalCase
import com.mcpy.lang.syntax.node.expression.Expression
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.context.Context

// Note: a chain link is a part of an expression that is separated by dots

abstract class ChainLink(
    val context: Context,
    val chains: List<ChainLink>,
    val firstToken: Token,
    index: Int,
) {

    class FunctionCall(
        val id: String,
        firstToken: Token,
        val parameters: List<GenericExpression> = emptyList(),
        private val cast: Type? = null,
    ) : Expression(firstToken) {
        override fun translate(context: Context): String {
            val inner = if (parameters.isEmpty()) {
                id
            } else {
                "$id(${parameters.joinToString { it.translate(context) }})"
            }
            return if (cast == null) {
                inner
            } else {
                "((${
                    Type.toJava(cast.type)?.value ?: error(
                        "An error has occurred trying to create a cast here",
                        firstToken
                    )
                }) $inner)"
            }
        }
    }

    protected val previous = chains.getOrNull(index - 1)
    protected val isFirst = (index == 0)

    abstract fun generate(): Pair<FunctionCall, Type>

    fun cast(func: FunctionCall, newType: Type) = FunctionCall(func.id, func.firstToken, func.parameters, newType)

}
fun validateSetter(
    baseClass: Type,
    methodName: Name,
    set: List<Token>,
    firstToken: Token
): ChainLink.FunctionCall? {
    return validateMethod(baseClass, Name("set${methodName.value.pascalCase()}", Name.NameType.FUNCTION), listOf(set), firstToken)
}

fun validateGetter(
    baseClass: Type,
    methodName: Name,
    firstToken: Token
): ChainLink.FunctionCall? {
    return validateMethod(baseClass, Name("get${methodName.value.pascalCase()}", Name.NameType.FUNCTION), null, firstToken)
}

fun validateMethod(
    baseClass: Type,
    methodName: Name,
    callArgs: List<List<Token>>?,
    firstToken: Token
): ChainLink.FunctionCall? {
    // TODO: This needs to account for getter and setter functions
    val method = baseClass.type.clazz.methods
        .firstOrNull { it.name == methodName.converted.value || it.name.endsWith(methodName.converted.value) }
        ?: return null
    val returnType = Type(method.genericReturnType.typeName)

    // TODO: This makes no sense and I need to go through all the use cases of this function and refactor them to use FunctionCall
    val id = Name(returnType.type.classNameFromQualifiedName(), Name.NameType.CLASS_NAME)
    val parameters = mutableListOf<GenericExpression>()
    if (callArgs != null) {
        parameters.addAll(callArgs.map { GenericExpression(it, it[0]) })
    }
    return ChainLink.FunctionCall(id.value, firstToken, parameters)
}