package com.rimlang.rim.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lexer {

    private static int line = -1;
    private static int lineCharacter = -1;

    private static int i = 0;

    public static List<Token> lex(String code) {
        line = -1;
        lineCharacter = -1;
        i = 0;
        List<Token> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        char[] charArray = code.toCharArray();
        while (i < charArray.length) {
            String string = String.valueOf(charArray[i]);
            if (string.matches("\\w")) {
                token.append(string);
            } else {
                if (token.length() > 0) {
                    String tokenString = token.toString();
                    if (tokenString.matches("-?\\d+(\\.\\d+)?")) {
                        tokens.add(new Token(TokenType.NUMBER_LITERAL, tokenString, line, lineCharacter));
                    } else {
                        TokenType type = TokenType.findType(tokenString); // Search for matching keywords
                        tokens.add(new Token(
                                Objects.requireNonNullElse(type, TokenType.ID),
                                tokenString,
                                line,
                                lineCharacter
                        ));
                    }
                    token = new StringBuilder();
                }
                if (string.equals("\n")) {
                    tokens.add(new Token(TokenType.EOL, "\\n", line, lineCharacter));
                    continue;
                }
                if (string.equals("#")) {
                    // Begin comment
                    while (!string.equals("\n")) {
                        nextCharacter(charArray);
                        string = String.valueOf(charArray[i]);
                    }
                    tokens.add(new Token(TokenType.EOL, "\\n", line, lineCharacter));
                    continue;
                }
                if (i == charArray.length - 1) {
                    tokens.add(new Token(TokenType.findType(string), string, line, lineCharacter));
                } else {
                    String doubleToken = string + charArray[i + 1];
                    TokenType doubleTokenType = TokenType.findType(doubleToken);
                    if (doubleTokenType == null) {
                        tokens.add(new Token(TokenType.findType(string), string, line, lineCharacter));
                    } else {
                        tokens.add(new Token(doubleTokenType, doubleToken, line, lineCharacter));
                        nextCharacter(charArray);
                        continue;
                    }
                }
                if (string.equals("'") || string.equals("\"")) {
                    String stringType = string;
                    // Begin string
                    nextCharacter(charArray);
                    string = String.valueOf(charArray[i]);
                    while (!string.equals(stringType)) {
                        token.append(string);
                        nextCharacter(charArray);
                        string = String.valueOf(charArray[i]);
                    }
                    tokens.add(new Token(TokenType.STRING_LITERAL, token.toString(), line, lineCharacter));
                    token = new StringBuilder();
                }
            }
            nextCharacter(charArray);
        }
        return tokens
                .stream()
                .filter(t -> t.type() != null)
                .toList();
    }

    private static void nextCharacter(char[] code) {
        i++;
        lineCharacter++;
        if (code[i] == '\n') {
            line++;
            lineCharacter = 0;
        }
    }

}
