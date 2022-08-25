package com.rimlang.rim.translation;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.rimlang.rim.Main;
import com.rimlang.rim.syntax.ComplexFunctionCallSyntaxNode;
import com.rimlang.rim.syntax.SyntaxNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Translator {

    private static final List<String> imports = new ArrayList<>();
    private static final List<String> events = new ArrayList<>();
    private static final List<String> methods = new ArrayList<>();
    private static final HashMap<String, String> commands = new HashMap<>();

    public static void translate(List<SyntaxNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            SyntaxNode node = nodes.get(i);
            if (node instanceof ComplexFunctionCallSyntaxNode functionCallSyntaxNode) {

            }
        }
    }

    public static String complete(String pluginName) throws FormatterException {
        String template = Main.loadResource("Template.java");
        assert template != null;
        String commandTemplate = "getCommand(\"%s\").setExecutor((sender, command, label, args) -> {%s});";
        List<String> cmds = commands.keySet().stream().map(n -> commandTemplate.formatted(n, commands.get(n))).toList();
        String complete = template.formatted(
                String.join(";\nimport ", imports),
                pluginName,
                String.join("\n", cmds),
                String.join("@EventHandler\n", events),
                String.join("\n\n", methods)
        );
        Formatter formatter = new Formatter();
        return formatter.formatSource(complete);
    }

}
