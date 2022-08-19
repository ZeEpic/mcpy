package com.rimlang.rim.util;

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
            if (t.type() == comma) {
                result.add(current);
                current = new ArrayList<>();
            } else {
                current.add(t);
            }
        }
        return result;
    }

    public static int next(List<Token> subList, TokenType type) {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < subList.size(); i++) {
            Token t = subList.get(i);
            assert type.getValues() != null;
            if (t.value().equals(type.getValues().get(0))) { // Open
                stack.push(i);
            } else if (t.value().equals(type.getValues().get(1))) { // Close
                if (stack.isEmpty()) return i;
                stack.pop();
            }
        }
        return -1;
    }


    public static List<Token> untilEOL(List<Token> tokens) {
        List<Token> result = new ArrayList<>();
        for (Token t : tokens) {
            if (t.type() == TokenType.EOL) break;
            result.add(t);
        }
        return result;
    }
}
