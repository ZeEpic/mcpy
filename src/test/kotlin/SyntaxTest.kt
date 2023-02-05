import com.rimlang.rim.lexer.Lexer
import com.rimlang.rim.syntax.*
import com.rimlang.rim.syntax.node.control.*
import com.rimlang.rim.syntax.node.global.CommandDefinitionSyntaxNode
import com.rimlang.rim.syntax.node.global.ComplexFunctionCallSyntaxNode
import com.rimlang.rim.syntax.node.global.FunctionDefinitionSyntaxNode
import com.rimlang.rim.syntax.node.syntax.VariableDefinitionSyntaxNode
import com.rimlang.rim.errors.CodeFile
import com.rimlang.rim.camelCase
import com.rimlang.rim.pascalCase
import com.rimlang.rim.snakeCase
import org.junit.jupiter.api.Test

object SyntaxTest {
    @Test
    fun testCases() {
        assert("camelCase".snakeCase() == "camel_case")
        assert("PascalCase".snakeCase() == "pascal_case")
        assert("snake_case".snakeCase() == "snake_case")
        assert("snake_case".pascalCase() == "SnakeCase")
        assert("camelCase".pascalCase() == "CamelCase")
        assert("PascalCase".pascalCase() == "PascalCase")
        assert("snake_case".camelCase() == "snakeCase")
        assert("camelCase".camelCase() == "camelCase")
        assert("PascalCase".camelCase() == "pascalCase")
    }

    private fun String.asFirstSyntaxNode() = analyze(Lexer.lex(CodeFile(this, "Test")), CodeFile(this, "Test")).firstOrNull()

    @Test
    fun testSyntax() {
        assert("if true {}".asFirstSyntaxNode() is IfSyntaxNode)
        assert("if true {} else {}".asFirstSyntaxNode() is IfSyntaxNode)
        assert("if true {} else if true {}".asFirstSyntaxNode() is IfSyntaxNode)
        assert("while true {}".asFirstSyntaxNode() is WhileSyntaxNode)
        assert("return true\n".asFirstSyntaxNode() is ReturnSyntaxNode)
        assert("for i in list {}".asFirstSyntaxNode() is ForeachSyntaxNode)
        assert("def test() {}".asFirstSyntaxNode() is FunctionDefinitionSyntaxNode)
        assert("def test(param: str) {}".asFirstSyntaxNode() is FunctionDefinitionSyntaxNode)
        assert("match test {}".asFirstSyntaxNode() is MatchSyntaxNode)
        assert("x = 10".asFirstSyntaxNode() is VariableDefinitionSyntaxNode)
        assert("x = test()".asFirstSyntaxNode() is VariableDefinitionSyntaxNode)
        assert("cmd test(sender: player) {}".asFirstSyntaxNode() is CommandDefinitionSyntaxNode)
        assert("cmd test(sender: player, param: string) {}".asFirstSyntaxNode() is CommandDefinitionSyntaxNode)
        assert("on(block.break) {}".asFirstSyntaxNode() is ComplexFunctionCallSyntaxNode)
        assert("on(block.break) { sender.sendMessage('test') }".asFirstSyntaxNode() is ComplexFunctionCallSyntaxNode)
    }
}

fun main() {
}