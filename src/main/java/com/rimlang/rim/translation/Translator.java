package com.rimlang.rim.translation;

import com.rimlang.rim.Main;
import com.rimlang.rim.lexer.NumberToken;
import com.rimlang.rim.lexer.StringToken;
import com.rimlang.rim.lexer.Token;
import com.rimlang.rim.lexer.TokenType;
import com.rimlang.rim.syntax.*;
import com.rimlang.rim.util.Strings;
import me.zeepic.JavaFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Translator {

    private static final List<String> imports = new ArrayList<>();
    private static final List<String> events = new ArrayList<>();
    private static final List<String> methods = new ArrayList<>();

    private static final List<String> fields = new ArrayList<>();

    private static final StringBuilder onEnable = new StringBuilder();

    private static final HashMap<String, HashMap<String, String>> traits = new HashMap<>(); // <class, <trait_name, type>>

    public static void translateGlobalScope(List<SyntaxNode> nodes) {
        String eventMapString = Main.loadResource("event_map.txt");
        HashMap<String, String> eventMap = new HashMap<>();
        assert eventMapString != null;
        for (String line : eventMapString.split("\n")) {
            String[] split = line.split(": ");
            eventMap.put(split[1], split[0]);
        }
        HashMap<String, Integer> eventCount = new HashMap<>();
        boolean hasTimer = false;
        for (SyntaxNode node : nodes) {
            if (node instanceof ComplexFunctionCallSyntaxNode) {
                var syntaxNode = (ComplexFunctionCallSyntaxNode) node;
                switch (syntaxNode.getFunctionIdentifier().getValue()) {
                    case "on" -> {
                        String event = syntaxNode.getArgs().get(0).getTokens().stream().map(t -> {
                            if (t instanceof StringToken) {
                                return ((StringToken) t).getValue();
                            }
                            else return "";
                        }).collect(Collectors.joining(""));
                        if (event.equals("server.start")) {
                            onEnable.append(translate(syntaxNode.getBody()));
                        } else {
                            String spigotEvent = eventMap.getOrDefault(event, "");
                            if (spigotEvent.isEmpty()) {
                                throw new IllegalArgumentException("Unknown event: " + event + " (at line " + syntaxNode.getArgs().get(0).getTokens().get(0).getLine() + ").");
                            }
                            if (!eventCount.containsKey(event)) {
                                eventCount.put(event, 0);
                            } else {
                                eventCount.put(event, eventCount.get(event) + 1);
                            }
                            String count = eventCount.get(event) + "";
                            if (count.equals("0")) count = "";
                            events.add("@EventHandler\npublic void on" + spigotEvent + count + "(" + spigotEvent + " event) {\n" + translate(syntaxNode.getBody()) + "\n}");
                        }
                    }
                    case "trait" -> {
                        Token traitType = syntaxNode.getArgs().get(0).getTokens().get(0);
                        if (traitType instanceof StringToken) {
                            String traitName = ((StringToken) traitType).getValue();
                            if (!traits.containsKey(traitName)) {
                                traits.put(traitName, new HashMap<>());
                            }
                            for (SyntaxNode bodyNode : syntaxNode.getBody()) {
                                if (bodyNode instanceof VariableDefinitionSyntaxNode) {
                                    var variableDefinition = (VariableDefinitionSyntaxNode) bodyNode;
                                    traits.get(traitName)
                                            .put(variableDefinition.getIdentifier().getValue(), variableDefinition.getInitialValue().getResultType());
                                }
                            }
                        }
                    }
                    case "timer" -> {
                        if (!hasTimer) {
                            hasTimer = true;
                            methods.add("""
                                    private void beginTimer(double seconds, Runnable runnable) {
                                        Bukkit.getScheduler().runTaskTimer(this, runnable, 0, (long) (20 * seconds));
                                    }""".indent(4));
                        }
                        Token secondsToken = syntaxNode.getArgs().get(0).getTokens().get(0);
                        if (secondsToken instanceof NumberToken) {
                            NumberToken numberToken = (NumberToken) secondsToken;
                            onEnable.append("beginTimer(")
                                    .append(numberToken.getValue())
                                    .append(", () -> {\n")
                                    .append(translate(syntaxNode.getBody()))
                                    .append("\n});\n");
                        } else {
                            throw new IllegalArgumentException("Timer's first argument for seconds must be a number (at line " + secondsToken.getLine() + ").");
                        }
                    }
                }
            } else if (node instanceof CommandDefinitionSyntaxNode) {
                var syntaxNode = (CommandDefinitionSyntaxNode) node;
                // TODO: Implement command arguments
                onEnable.append("getCommand(\"")
                        .append(getCamelCase(syntaxNode.getIdentifier().getValue()))
                        .append("\").setExecutor((sender, command, label, args) -> {\n")
                        .append(translate(syntaxNode.getBody()))
                        .append("\n});");
            } else if (node instanceof FunctionDefinitionSyntaxNode) {
                var syntaxNode = (FunctionDefinitionSyntaxNode) node;
                methods.add("private " + toJavaType(syntaxNode.getReturnType()) + " " + getCamelCase(syntaxNode.getIdentifier().getValue()) + "(" + syntaxNode.getArgs().translate() + ") {\n" + translate(syntaxNode.getBody()) + "\n}");
            } else if (node instanceof VariableDefinitionSyntaxNode) {
                var syntaxNode = (VariableDefinitionSyntaxNode) node;
                fields.add("private var " + getCamelCase(syntaxNode.getIdentifier().getValue()) + " = " + syntaxNode.getInitialValue().translate() + ";");
            } else {
                throw new IllegalStateException("Node type " + node.getClass().getName() + " is not allowed in global scope.");
            }
        }
    }

    public static String toJavaType(StringToken typeToken) {
        String type = typeToken.getValue();
        if (type.startsWith("list[") && type.endsWith("]")) {
            return "List<" + toJavaType(new StringToken(
                    TokenType.TYPE,
                    type.substring(5, type.length() - 1),
                    typeToken.getLine(),
                    typeToken.getCharacter()
            )) + ">";
        }
        switch (type) {
            case "str":
                return "String";
            case "num":
                return "double";
            case "bool":
                return "boolean";
            case "void":
                return "void";
            case "player":
                return "Player";
        }
        try {
            throw new ClassNotFoundException("Type " + type + " not found (on line " + typeToken.getLine() + ").");
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getCamelCase(String value) {
        String pascalCase = getPascalCase(value);
        return pascalCase.substring(0, 1).toLowerCase() + pascalCase.substring(1);
    }

    @NotNull
    private static String getPascalCase(String value) {
        return Arrays.stream(value.split("_"))
                .map(Strings::title)
                .collect(Collectors.joining());
    }

    public static String translate(List<SyntaxNode> nodes) {
        StringBuilder builder = new StringBuilder();
        for (SyntaxNode node : nodes) {
            if (node instanceof IfSyntaxNode) {
                var syntaxNode = (IfSyntaxNode) node;
                builder.append("if (")
                        .append(syntaxNode.getBooleanExpression().translate())
                        .append(") {\n")
                        .append(translate(syntaxNode.getBody()))
                        .append("\n}\n");
            } else if (node instanceof ExpressionSyntaxNode) {
                var syntaxNode = (ExpressionSyntaxNode) node;
            } else if (node instanceof ForeachSyntaxNode) {
                var syntaxNode = (ForeachSyntaxNode) node;
                builder.append("for (");
            } else if (node instanceof WhileSyntaxNode) {
                var syntaxNode = (WhileSyntaxNode) node;
            } else if (node instanceof ReturnSyntaxNode) {
                var syntaxNode = (ReturnSyntaxNode) node;
            } else if (node instanceof VariableDefinitionSyntaxNode) {
                var syntaxNode = (VariableDefinitionSyntaxNode) node;
            }
        }
        return builder.toString();
    }

    public static String complete(String pluginName) {
        String template = Main.loadResource("Template.java");
        assert template != null;
        String complete = template.formatted(
                String.join(";\nimport ", imports),
                pluginName,
                String.join("\n", fields),
                onEnable.toString(),
                String.join("\n", events),
                String.join("\n\n", methods)
        );
        return JavaFormatter.format(complete);
    }

}
