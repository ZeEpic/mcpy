package com.mcpy.lang.lexer

import com.mcpy.lang.compiler.CompilerPhase
import com.mcpy.lang.errors.CodeFile
import com.mcpy.lang.lexer.token.*
import java.util.*

class Lexer : CompilerPhase {

    private var line = -1
    private var lineCharacter = -1
    private var i = 0

    fun lex(code: CodeFile): List<Token> {
        line = 0
        lineCharacter = 0
        i = 0
        val tokens = mutableListOf<Token>()
        var token = ""
        val charArray = code.text.toCharArray()
        while (i < charArray.size) {

            // Add to token
            var string = charArray[i].toString()
            if (string matches Regex("\\w")) {
                token += string
                if (nextCharacter(charArray)) break
                continue
            }

            // Identifier or number
            if (token.isNotEmpty()) {
                if (token matches Regex("-?\\d+(\\.\\d+)?")) {
                    tokens.add(NumberToken(TokenType.NUMBER_LITERAL, token.toDouble(), line, lineCharacter, code))
                } else {
                    val type = findTokenType(token) // Search for matching keywords
                    tokens.add(
                        StringToken(
                            type ?: TokenType.ID,
                            token,
                            line,
                            lineCharacter,
                            code
                        )
                    )
                }
                token = ""
            }

            // New line
            if (string == "\n") {
                tokens.add(StringToken(TokenType.EOL, "\\n", line, lineCharacter, code))
                if (nextCharacter(charArray)) break
                continue
            }

            // Comment
            if (string == "#") {
                while (string != "\n") {
                    if (nextCharacter(charArray)) break
                    string = charArray[i].toString()
                }
                tokens.add(StringToken(TokenType.EOL, "\\n", line, lineCharacter, code))
                if (nextCharacter(charArray)) break
                continue
            }

            // String literal
            if (string == "'" || string == "\"") {
                val stringType = string
                if (nextCharacter(charArray)) break
                string = charArray[i].toString()
                while (string != stringType) {
                    token += string
                    if (nextCharacter(charArray)) break
                    string = charArray[i].toString()
                }
                tokens.add(StringToken(TokenType.STRING_LITERAL, token, line, lineCharacter, code))
                token = ""
            }

            // Check for last character
            val type = findTokenType(string)
            if (i == charArray.size - 1) {
                if (type != null) {
                    tokens.add(StringToken(type, string, line, lineCharacter, code))
                }
                if (nextCharacter(charArray)) break
                continue
            }

            // Two character tokens
            val doubleToken = string + charArray[i + 1]
            val doubleTokenType = findTokenType(doubleToken)
            if (doubleTokenType == null) {
                if (type != null) {
                    tokens.add(StringToken(type, string, line, lineCharacter, code))
                }
                if (nextCharacter(charArray)) break
                continue
            }
            tokens.add(StringToken(doubleTokenType, doubleToken, line, lineCharacter, code))
            if (nextCharacter(charArray)) break
            if (nextCharacter(charArray)) break
        }
        return groupBrackets(tokens)
    }

    private fun groupBrackets(code: List<Token>): List<Token> {
        val groupedCode = mutableListOf<Token>()
        var j = 0
        while (j < code.size) {
            val token = code[j]
            val value = token.value
            if (value in listOf("{", "[", "(")) {
                val end = code.nextToken(j + 1, token.type)
                val group = GroupToken(
                    token.type,
                    groupBrackets(code.subList(j + 1, end)),
                    token.line,
                    token.character,
                    token.file
                )
                groupedCode.add(group)
                j = end + 1
                continue
            }
            groupedCode.add(token)
            j++
        }
        return groupedCode
    }

    private fun nextCharacter(code: CharArray): Boolean {
        if (i >= code.size - 1) return true
        i++
        lineCharacter++
        if (code[i] == '\n') {
            line++
            lineCharacter = 0
        }
        return false
    }

    private fun List<Token>.nextToken(from: Int, type: TokenType, end: Int = this.size): Int {
        val stack = Stack<Int>()
        for (i in from until this.size) {
            val t = this[i]
            if (t.type !== type) continue
            if (t.value == type.values[0]) { // Open
                stack.push(i)
            } else if (t.value == type.values[1]) { // Close
                if (stack.isEmpty()) return i
                stack.pop()
            }
            if (i >= end) break
        }
        return -1
    }

}