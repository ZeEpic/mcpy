package com.rimlang.rim.lexer

enum class TokenType(vararg values: String) {
    ID, NUMBER_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL("true", "false"), EOL("\\n"), BOOLEAN_OPERATOR(
        ">",
        "<",
        ">=",
        "<=",
        "!=",
        "=="
    ),
    MATH_OPERATOR("+", "-", "*", "/", "%"), LOGICAL_OPERATOR("and", "or", "not"), ASSIGNMENT_OPERATOR("="), PARENTHESES(
        "(",
        ")"
    ),
    BRACKET("[", "]"), BRACE(
        "{",
        "}"
    ),
    COMMA(","), DOT("."), COLON(":"), AT("@"), IF("if"), ELSE("else"), ELIF("elif"), WHILE("while"), FOR("for"), MATCH("match"), IN(
        "in"
    ),
    RETURN("return"), FUNCTION("fn"), PASS("pass"), COMMAND("cmd"), IS("is"), BY("by"), TYPE(
        "num", "string", "bool", "list", "function",
        "player", "location", "world", "entity",
        "event", "args",
        "block", "item", "material",
        "permission"
    );
    val values = values.toList()
}

fun findTokenType(type: String)
    = TokenType.values().firstOrNull { type in it.values }
