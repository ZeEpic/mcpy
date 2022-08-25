package com.rimlang.rim.util;

import com.rimlang.rim.lexer.GroupToken;
import com.rimlang.rim.lexer.Token;
import com.rimlang.rim.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TokenUtil {
    public static List<List<Token>> split(List<Token> tokens, TokenType comma) {
        List<List<Token>> result = new ArrayList<>();
        List<Token> current = new ArrayList<>();
        for (Token t : tokens) {
            if (t.getType() == comma) {
                result.add(current);
                current = new ArrayList<>();
            } else {
                current.add(t);
            }
        }
        if (!current.isEmpty()) result.add(current);
        return result;
    }

    public static int next(List<Token> tokens, int from, int end, TokenType type) {
        Stack<Integer> stack = new Stack<>();
        for (int i = from; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            assert type.getValues() != null;
            if (t.getType() != type) continue;
            if (t.value().equals(type.getValues().get(0))) { // Open
                stack.push(i);
            } else if (t.value().equals(type.getValues().get(1))) { // Close
                if (stack.isEmpty()) return i;
                stack.pop();
            }
        }
        return -1;
    }

    public static int next(List<Token> tokens, int from, TokenType type) {
        return next(tokens, from, tokens.size(), type);
    }

//    public static int next(List<Token> tokens, TokenType type) {
//        return next(tokens, 0, type);
//    }

    public static int nextBrace(List<Token> tokens, int from) {
        return next(tokens, from, TokenType.BRACE);
    }

    public static int nextBrace(List<Token> tokens, int from, int end) {
        return next(tokens, from, end, TokenType.BRACE);
    }



    public static List<Token> untilEOL(List<Token> tokens) {
        List<Token> result = new ArrayList<>();
        for (Token t : tokens) {
            if (t.getType() == TokenType.EOL) break;
            result.add(t);
        }
        return result;
    }

    public static int after(List<Token> code, int index) {
        int i = index;
        if (code.size() >= i) return index;
        while (code.get(i + 1).getType() == TokenType.EOL)
            i++;
        return index + 1;
    }

    public static int findEOL(List<Token> code, int index) {
        int i = index;
        while (i < code.size() && code.get(i).getType() != TokenType.EOL)
            i++;
        return i;
    }

    public static <E> List<E> sub(List<E> list, int from, int end) {
        return new ArrayList<>(list).subList(from, end);
    }

    public static <E> List<E> sub(List<E> list, int from) {
        return sub(list, from, list.size());
    }

    public static List<Token> getGroup(Token token) {
        if (token instanceof GroupToken groupToken) {
            return groupToken.getValue();
        }
        return null;
    }
}
