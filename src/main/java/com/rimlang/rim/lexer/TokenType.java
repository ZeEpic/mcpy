package com.rimlang.rim.lexer;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public enum TokenType {
    ID(null), NUMBER_LITERAL(null), STRING_LITERAL(null), BOOLEAN_LITERAL(List.of("true", "false")),
    EOL(List.of("\\n")),
    BOOLEAN_OPERATOR(List.of( ">", "<", ">=", "<=", "!=", "==", "")),
    MATH_OPERATOR(List.of("+", "-", "*", "/", "%")),
    LOGICAL_OPERATOR(List.of("and", "or", "not")),
    ASSIGNMENT_OPERATOR(List.of("=")),
    PARENTHESIS(List.of("(", ")")), BRACKET(List.of("[", "]")), BRACE(List.of("{", "}")),
    COMMA(List.of(",")), DOT(List.of(".")), COLON(List.of(":")),
    AT(List.of("@")),
    IF(List.of("if")), ELSE(List.of("else")), ELIF(List.of("elif")),
    WHILE(List.of("while")), FOR(List.of("for")), MATCH(List.of("match")),
    PRINT(List.of("print")),
    RETURN(List.of("return")), FUNCTION(List.of("fn")), PASS(List.of("pass")),
    IS(List.of("is")), BY(List.of("by")),
    COMMAND(List.of("command")), TIMER(List.of("timer")), ON_EVENT(List.of("on")), TRAIT(List.of("trait")),
    TYPE(List.of(
            "int", "string", "bool", "list", "function",
            "player", "location", "world", "entity",
            "event", "args",
            "block", "item", "material",
            "permission"
    ));
    private final @Nullable List<String> values;

    TokenType(@Nullable List<String> values) {
        this.values = values;
    }

    public static @Nullable TokenType findType(String s) {
        AtomicReference<TokenType> result = new AtomicReference<>();
        Arrays.stream(TokenType.values())
                .filter(t -> t.getValues() != null && t.getValues().contains(s))
                .findFirst()
                .ifPresent(result::set);
        return result.get();
    }

    public @Nullable List<String>getValues() {
        return values;
    }

}
