package com.rimlang.rim.syntax;

import com.rimlang.rim.lexer.Token;
import com.rimlang.rim.lexer.TokenType;
import com.rimlang.rim.util.Strings;
import com.rimlang.rim.util.TokenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SyntaxAnalyzer {

    public static List<SyntaxNode> analyze(List<Token> code) throws RimSyntaxException {
        List<SyntaxNode> result = new ArrayList<>();
        List<Token> codeLine = new ArrayList<>();
        int beginningOfLine = 0;
        for (int i = 0; i < code.size(); i++) {
            Token t = code.get(i);
            if (t.getType() != TokenType.EOL) {
                codeLine.add(t);
            }
            if (t.getType() == TokenType.EOL || i == code.size() - 1) {
                if (codeLine.isEmpty()) continue;
                result.add(processLine(codeLine, code, codeLine.get(0).getLine(), beginningOfLine));
                codeLine.clear();
                beginningOfLine = i + 1;
            }
        }
        return result;
    }

    private static @NotNull SyntaxNode processLine(List<Token> codeLine, List<Token> code, int line, int beginningOfLine) throws RimSyntaxException {
        TokenType first = codeLine.get(0).getType();
        switch(first) {
            case FUNCTION, COMMAND -> {
                String lineType = Strings.title(first.name());
                if (codeLine.get(1).getType() != TokenType.ID) {
                    throw new RimSyntaxException(lineType + " definition must have a name", line);
                }
                Token name = codeLine.get(1);
                if (codeLine.get(2).getType() != TokenType.PARENTHESES) {
                    throw new RimSyntaxException(lineType + " definition must have parentheses, even if there are zero arguments", line);
                }
                ArgsExpression args = new ArgsExpression(TokenUtil.getGroup(codeLine.get(2)));
                Token body = codeLine.get(codeLine.size() - 1);
                if (body.getType() != TokenType.BRACE) {
                    throw new RimSyntaxException(lineType + " must have a code body", line);
                }
                List<SyntaxNode> syntaxNodes = analyze(TokenUtil.getGroup(body));
                if (first == TokenType.COMMAND) { // Command doesn't have a return type
                    return new CommandDefinitionSyntaxNode(name, args, syntaxNodes);
                }

                Token returnType = null;
                if (code.get(3).getType() == TokenType.COLON) {
                    if (code.get(4).getType() != TokenType.TYPE) {
                        throw new RimSyntaxException(lineType + " has a colon, but no return type", line);
                    }
                    returnType = code.get(4);
                }
                return new FunctionDefinitionSyntaxNode(name, args, returnType, syntaxNodes);
            }
            case ID -> {
                TokenType second = codeLine.get(1).getType();
                if (second == TokenType.ASSIGNMENT_OPERATOR) {
                    return new VariableDefinitionSyntaxNode(codeLine.get(0), new GenericExpression(TokenUtil.sub(codeLine, 2, codeLine.size())));
                } else if (second == TokenType.DOT) {
                    return new ExpressionSyntaxNode(codeLine);
                } else if (second == TokenType.PARENTHESES) {
                    List<Token> argTokens = TokenUtil.getGroup(codeLine.get(1));
                    assert argTokens != null;
                    List<GenericExpression> args = TokenUtil.split(argTokens, TokenType.COMMA)
                            .stream().map(GenericExpression::new).toList();
                    if (codeLine.size() <= 2 || codeLine.get(2).getType() != TokenType.BRACE) {
                        return new ExpressionSyntaxNode(codeLine);
                    }
                    List<Token> tokens = TokenUtil.getGroup(codeLine.get(2));
                    assert tokens != null;
                    return new ComplexFunctionCallSyntaxNode(
                            codeLine.get(0),
                            args,
                            analyze(tokens)
                    );
                }
            }
            case IF -> {
                Token body = codeLine.get(codeLine.size() - 1);
                if (body.getType() != TokenType.BRACE) {
                    throw new RimSyntaxException("If statement must have code body", line);
                }
                // TODO: Add else and elif
                return new IfSyntaxNode(
                        new BooleanExpression(TokenUtil.sub(codeLine, 1, codeLine.size() - 1)),
                        analyze(TokenUtil.getGroup(body))
                );
            }
            case WHILE -> {
                Token body = codeLine.get(codeLine.size() - 1);
                if (body.getType() != TokenType.BRACE) {
                    throw new RimSyntaxException("While statement must have code body", line);
                }
                return new WhileSyntaxNode(
                        new BooleanExpression(TokenUtil.sub(codeLine, 1, codeLine.size() - 1)),
                        analyze(TokenUtil.getGroup(body))
                );
            }
            case MATCH -> {
                // TODO: Fix match statement, it's using the old system
                int bodyStart = TokenUtil.nextBrace(code, beginningOfLine);
                int bodyEnd = TokenUtil.nextBrace(code, bodyStart + 1);
                int branchStart = TokenUtil.after(code, bodyEnd);
                HashMap<Token, List<SyntaxNode>> branches = new HashMap<>();
                while ((code.get(branchStart).getType() == TokenType.ID
                        || code.get(branchStart).getType() == TokenType.ELSE)
                        && branchStart < bodyEnd)
                {
                    int branchEnd = TokenUtil.nextBrace(code, branchStart + 1, bodyEnd);
                    branches.put(code.get(branchStart), analyze(TokenUtil.sub(code, bodyStart, branchEnd)));
                    bodyStart = TokenUtil.after(code, branchEnd);
                }
                return new MatchSyntaxNode(
                        new GenericExpression(TokenUtil.sub(code, beginningOfLine + 1, bodyStart)),
                        branches
                );
            }
            case RETURN -> {
                return new ReturnSyntaxNode(new GenericExpression(TokenUtil.sub(code, beginningOfLine + 1)));
            }
            case FOR -> {
                Token body = codeLine.get(codeLine.size() - 1);
                if (body.getType() != TokenType.BRACE) {
                    throw new RimSyntaxException("For each statement must have code body", line);
                }
                if (codeLine.get(2).getType() != TokenType.IN) {
                    throw new RimSyntaxException("For loop must have 'in' token", line);
                }
                return new ForeachSyntaxNode(
                        codeLine.get(1),
                        new GenericExpression(TokenUtil.sub(codeLine, 3, codeLine.size() - 1)),
                        analyze(TokenUtil.getGroup(body))
                );
            }
        }
        System.out.println(codeLine);
        throw new RimSyntaxException("Illegal start to line", line);
    }

}
