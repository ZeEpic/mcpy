package com.mcpy.lang.errors

import com.mcpy.lang.lexer.token.Token
import org.bukkit.Bukkit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.max

data class McPyError(
    val message: String,
    val line: Int,
    val column: Int,
    val file: CodeFile,
) {
    private fun getSpacing(): Int {
        val before = 0.coerceAtLeast(line - 2)
        return (max(before, line) + 1).toString().length
    }

    private fun getLines(windowSize: Int, useBukkitColors: Boolean): List<String> {
        val lines = mutableListOf<String>()

        val before = 0.coerceAtLeast(line - windowSize + 1)
        val length = getSpacing()

        val white = if (useBukkitColors) "&7" else "\u001b[37;1m"
        val blue = if (useBukkitColors) "&b" else "\u001b[34m"
        val reset = if (useBukkitColors) "&r" else "\u001b[0m"

        for (i in before..line) {
            val numberLength = (i+1).toString().length
            val prefix = if (numberLength < length) " ".repeat(length - numberLength) else ""
            lines.add("$blue${i+1}$prefix$white │ $reset${file.readLine(i)}")
        }

        return lines
    }

    fun consolePrint(useBukkitColors: Boolean) {
        val lines = getLines(3, useBukkitColors)
        val spaces = getSpacing()

        val red = if (useBukkitColors) "&c" else "\u001b[31m"
        val reset = if (useBukkitColors) "&r" else "\u001b[0m"
        val white = if (useBukkitColors) "&7" else "\u001b[37;1m"
        val blue = if (useBukkitColors) "&b" else "\u001b[34m"

        val message = listOf(
            "\n$white ——> $blue${file.fileName}$white:$blue${line + 1}$white:$blue$column$reset",
            lines.joinToString("\n"),
            "${" ".repeat(spaces + 2 + column)}$red ^ $message"
        ).joinToString("\n")
        if (useBukkitColors) {
            Bukkit.getLogger().info(message)
            return
        }
        println(message)
    }

}

fun error(message: String, token: Token): Nothing {
    val error = McPyError(message, token.line, token.character, token.file)
    error.consolePrint(true)
    throw Exception()
}

@OptIn(ExperimentalContracts::class)
fun require(condition: Boolean, token: Token, message: () -> String) {
    contract {
        returns() implies condition // only continues the code if the condition is true and the requirement is met
    }
    if (!condition) {
        error(message(), token)
    }
}