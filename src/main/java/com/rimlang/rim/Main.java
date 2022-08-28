package com.rimlang.rim;

import com.rimlang.rim.lexer.Lexer;
import com.rimlang.rim.lexer.Token;
import com.rimlang.rim.syntax.RimSyntaxException;
import com.rimlang.rim.syntax.SyntaxAnalyzer;
import com.rimlang.rim.syntax.SyntaxNode;
import com.rimlang.rim.translation.Translator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            main();
        } catch (RimSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void main() throws RimSyntaxException, IOException {
        String code = loadResource("code.rim");
        if (code == null) {
            System.out.println("Could not load code.rim");
            return;
        }
        List<Token> tokens = Lexer.lex(code);
        List<SyntaxNode> nodes = SyntaxAnalyzer.analyze(tokens);
//        Compiler.compile(javaCode);
//        JarMaker.create();
        Translator.translateGlobalScope(nodes);
        FileWriter output = new FileWriter("ExamplePlugin.java", false);
        output.write((Translator.complete("ExamplePlugin")));
        output.close();
    }

    public static @Nullable String loadResource(@NotNull String resource) {
        InputStream stream = Main.class.getResourceAsStream("/" + resource);
        if (stream == null) return null;
        return new BufferedReader(new InputStreamReader(stream))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}