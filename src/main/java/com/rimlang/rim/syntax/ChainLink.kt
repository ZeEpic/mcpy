package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.StringToken
import com.rimlang.rim.lexer.Token
import com.rimlang.rim.translation.*
import com.rimlang.rim.util.camelCase
import com.rimlang.rim.util.classNameFromQualifiedName
import com.rimlang.rim.util.clazz
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

data class ChainLink(
    val idToken: StringToken,
    val callArgs: List<List<Token>>?,
    val context: Context,
    val chains: List<ChainLink>,
    val i: Int
) {

    private val previous = chains.getOrNull(i - 1)
    private val isFirst = (i == 0)

    lateinit var returnType: String
    lateinit var idInJava: String
    val parametersInJava = mutableListOf<GenericExpression>()

    init {
        generate()
    }

    private fun generate() {
        val id = idToken.value
        if (isFirst) {
            val bukkitObjectClass = when (id) {
                "server" -> Bukkit::class.qualifiedName
                "entity" -> EntityType::class.qualifiedName
                else -> null
            }
            if (bukkitObjectClass != null) {
                returnType = bukkitObjectClass
                idInJava = bukkitObjectClass.classNameFromQualifiedName()
                return
            }
            if (id in Material.values().map(Material::name)) {
                returnType = Material::class.qualifiedName!!
                idInJava = "Material.$id"
                return
            }
            val matchingFunction = context.identifiers.filterIsInstance<FunctionIdentifier>().firstOrNull { it.name == id }
            if (callArgs != null && matchingFunction != null) {
                val args = callArgs.map { GenericExpression(it) }
                returnType = matchingFunction.returnType
                idInJava = matchingFunction.name.camelCase()
                parametersInJava.addAll(args)
                return
            }
            val matchingVariable = context.identifiers.filterIsInstance<VariableIdentifier>().firstOrNull { it.name == id }
            if (matchingVariable != null) {
                returnType = matchingVariable.type
                idInJava = matchingVariable.name.camelCase()
                return
            }
            when (context) {
                is EventContext -> {
                    val eventClass = context.event
                    if (eventClass != "onEnable") {
                        validateMethod(eventClass, id, callArgs)
                    }
                }
                is CommandContext -> {
                    if (id == "player") {
                        returnType = Player::class.qualifiedName!!
                        idInJava = "sender"
                        return
                    }
                    if (id == "args") {
                        returnType = "String[]"
                        idInJava = "args"
                    }
                }
                is TimerContext -> {

                }
                is VariableDefinitionContext -> {

                }
            }
            throw IllegalStateException("Could not find identifier $id, at line ${idToken.line}")
        }
        if (previous != null) {
            // TODO: Verify that the previous return type has trait with this name
            if (previous.returnType == Block::class.qualifiedName) {
                // TODO: Get persistent data container value
            } else if (Entity::class.java.isAssignableFrom(previous.returnType.clazz)) { // if previous return type is an Entity
                // Same thing as above
            } else {
                validateMethod(previous.returnType, id, callArgs)
            }
        }
        throw IllegalStateException("Could not find identifier $id, at line ${idToken.line}")
    }

    private fun validateMethod(
        baseClass: String,
        methodName: String,
        callArgs: List<List<Token>>?
    ) {
        val method = baseClass.clazz.methods
            .filter { it.isDefault }
            .firstOrNull { it.name == methodName.camelCase() } ?: return
        returnType = method.genericReturnType.typeName
        idInJava = returnType.classNameFromQualifiedName()
        // TODO: Add getters and settings
        if (callArgs != null) {
            parametersInJava.addAll(callArgs.map { GenericExpression(it) })
        }
    }
}