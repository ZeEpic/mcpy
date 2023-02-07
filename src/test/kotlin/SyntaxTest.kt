import com.mcpy.lang.camelCase
import com.mcpy.lang.pascalCase
import com.mcpy.lang.snakeCase
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
}

fun main() {
}