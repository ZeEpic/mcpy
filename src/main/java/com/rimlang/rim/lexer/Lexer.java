package com.rimlang.rim.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lexer {

    public static List<Token> lex(String code) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        char[] charArray = code.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            String string = String.valueOf(charArray[i]);
            if (string.matches("\\w")) {
                token.append(string);
            } else {
                if (token.length() > 0) {
                    String tokenString = token.toString();
                    if (tokenString.matches("-?\\d+(\\.\\d+)?")) {
                        tokens.add(new Token(TokenType.NUMBER_LITERAL, tokenString));
                    } else {
                        TokenType type = TokenType.findType(tokenString);
                        if (type == null) {
                            tokens.add(new Token(TokenType.ID, tokenString));
                        } else {
                            tokens.add(new Token(type, tokenString));
                        }
                    }
                    token = new StringBuilder();
                }
                if (string.equals("\n")) {
                    tokens.add(new Token(TokenType.EOL, "\\n"));
                    continue;
                }
                if (string.equals("/") && charArray[i + 1] == '/') {
                    // Begin comment
                    while (!string.equals("\n")) {
                        i++;
                        string = String.valueOf(charArray[i]);
                    }
                    tokens.add(new Token(TokenType.EOL, "\\n"));
                    continue;
                }
                if (i == charArray.length - 1) {
                    tokens.add(new Token(TokenType.findType(string), string));
                } else {
                    String doubleToken = string + charArray[i + 1];
                    TokenType doubleTokenType = TokenType.findType(doubleToken);
                    if (doubleTokenType == null) {
                        tokens.add(new Token(TokenType.findType(string), string));
                    } else {
                        tokens.add(new Token(doubleTokenType, doubleToken));
                        i++;
                        continue;
                    }
                }
                if (string.equals("'") || string.equals("\"")) {
                    String stringType = string;
                    // Begin string
                    i++;
                    string = String.valueOf(charArray[i]);
                    while (!string.equals(stringType)) {
                        token.append(string);
                        i++;
                        string = String.valueOf(charArray[i]);
                    }
                    tokens.add(new Token(TokenType.STRING_LITERAL, token.toString()));
                    token = new StringBuilder();
                }
            }
        }
        return tokens
                .stream()
                .filter(t -> t.type() != null)
                .toList();
    }

}
