package com.mcpy.lang.syntax.chain

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.classNameFromQualifiedName
import com.mcpy.lang.clazz
import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.Token
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.translation.Events
import com.mcpy.lang.translation.context.*
import com.mcpy.lang.translation.identifier.VariableIdentifier
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

abstract class ChainLink(
    val context: Context,
    val chains: List<ChainLink>,
    index: Int,
) {

    protected val previous = chains.getOrNull(index - 1)
    protected val isFirst = (index == 0)

    lateinit var returnType: Type
    lateinit var idInJava: Name
    val parametersInJava = mutableListOf<GenericExpression>()

    abstract fun generate()

    protected fun validateMethod(
        baseClass: Type,
        methodName: Name,
        callArgs: List<List<Token>>?
    ) {
        val method = baseClass.type.clazz.methods
            .filter { it.isDefault }
            .firstOrNull { it.name == methodName.convertedValue.value || it.name.endsWith(methodName.convertedValue.value) } ?: return
        returnType = Type(method.genericReturnType.typeName)
        idInJava = Name(returnType.type.classNameFromQualifiedName(), Name.NameType.CLASS_NAME)
        if (callArgs != null) {
            parametersInJava.addAll(callArgs.map { GenericExpression(it, it[0]) })
        }
    }
}