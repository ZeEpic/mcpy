package com.rimlang.rim.lexer;

import com.rimlang.rim.util.TokenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Lexer {

    private static int line = -1;
    private static int lineCharacter = -1;

    private static int i = 0;

    public static List<Token> lex(String code) {
        line = 0;
        lineCharacter = 0;
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
                        tokens.add(new NumberToken(TokenType.NUMBER_LITERAL, Double.parseDouble(tokenString), line, lineCharacter));
                    } else {
                        TokenType type = TokenType.findType(tokenString); // Search for matching keywords
                        tokens.add(new StringToken(
                                Objects.requireNonNullElse(type, TokenType.ID),
                                tokenString,
                                line,
                                lineCharacter
                        ));
                    }
                    token = new StringBuilder();
                }
                if (string.equals("\n")) {
                    tokens.add(new StringToken(TokenType.EOL, "\\n", line, lineCharacter));
                    if (nextCharacter(charArray)) break;
                    continue;
                }
                if (string.equals("#")) {
                    // Begin comment
                    while (!string.equals("\n")) {
                        if (nextCharacter(charArray)) break;
                        string = String.valueOf(charArray[i]);
                    }
                    tokens.add(new StringToken(TokenType.EOL, "\\n", line, lineCharacter));
                    if (nextCharacter(charArray)) break;
                    continue;
                }
                if (i == charArray.length - 1) {
                    tokens.add(new StringToken(TokenType.findType(string), string, line, lineCharacter));
                } else {
                    String doubleToken = string + charArray[i + 1];
                    TokenType doubleTokenType = TokenType.findType(doubleToken);
                    if (doubleTokenType == null) {
                        tokens.add(new StringToken(TokenType.findType(string), string, line, lineCharacter));
                    } else {
                        tokens.add(new StringToken(doubleTokenType, doubleToken, line, lineCharacter));
                        if (nextCharacter(charArray)) break;
                        if (nextCharacter(charArray)) break;
                        continue;
                    }
                }
                if (string.equals("'") || string.equals("\"")) {
                    String stringType = string;
                    // Begin string
                    if (nextCharacter(charArray)) break;
                    string = String.valueOf(charArray[i]);
                    while (!string.equals(stringType)) {
                        token.append(string);
                        if (nextCharacter(charArray)) break;
                        string = String.valueOf(charArray[i]);
                    }
                    tokens.add(new StringToken(TokenType.STRING_LITERAL, token.toString(), line, lineCharacter));
                    token = new StringBuilder();
                }
            }
            if (nextCharacter(charArray)) break;
        }
        return groupBrackets(tokens
                .stream()
                .filter(t -> t.getType() != null)
                .collect(Collectors.toList()));
    }

    public static List<Token> groupBrackets(List<Token> code) {
        List<Token> groupedCode = new ArrayList<>();
        for (int j = 0; j < code.size(); j++) {
            Token token = code.get(j);
            Object value = token.value();
            if ("{".equals(value) || "[".equals(value) || "(".equals(value)) {
                int end = TokenUtil.next(code, j + 1, token.getType());
                Token group = new GroupToken(token.getType(),
                        groupBrackets(TokenUtil.sub(code, j + 1, end)),
                        token.getLine(),
                        token.getCharacter()
                );
                groupedCode.add(group);
                j = end;
                continue;
            }
            groupedCode.add(token);
        }
        return groupedCode;
    }

    private static boolean nextCharacter(char[] code) {
        if (i >= code.length - 1) return true;
        i++;
        lineCharacter++;
        if (code[i] == '\n') {
            line++;
            lineCharacter = 0;
        }
        return false;
    }

}
