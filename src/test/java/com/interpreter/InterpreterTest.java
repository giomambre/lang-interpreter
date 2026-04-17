package com.interpreter;

import com.interpreter.ast.Stmt;
import com.interpreter.ast.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {

    // Helper: runs the full pipeline and returns the global environment
    private Map<String, Integer> run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        List<Stmt> stmts = parser.parse();
        Interpreter interpreter = new Interpreter();
        return interpreter.interpret(stmts);
    }

    @Test
    void testSimpleAssignment() {
        Map<String, Integer> env = run("""
                x = 2
                y = (x + 2) * 2
                """);
        assertEquals(2, env.get("x"));
        assertEquals(8, env.get("y"));
    }

    @Test
    void testIfThenElse() {
        Map<String, Integer> env = run("""
                x = 20
                if x > 10 then y = 100 else y = 0
                """);
        assertEquals(20, env.get("x"));
        assertEquals(100, env.get("y"));
    }

    @Test
    void testWhileWithIf() {
        Map<String, Integer> env = run("""
                x = 0
                y = 0
                while x < 3 do if x == 1 then y = 10 else y = y + 1, x = x + 1
                """);
        assertEquals(3, env.get("x"));
        assertEquals(11, env.get("y"));
    }

    @Test
    void testFunctionCall() {
        Map<String, Integer> env = run("""
                fun add(a, b) { return a + b }
                four = add(2, 2)
                """);
        assertEquals(4, env.get("four"));
    }

    @Test
    void testRecursiveFactorial() {
        Map<String, Integer> env = run("""
                fun fact_rec(n) { if n <= 0 then return 1 else return n*fact_rec(n-1) }
                a = fact_rec(5)
                """);
        assertEquals(120, env.get("a"));
    }

    @Test
    void testIterativeFactorial() {
        Map<String, Integer> env = run("""
                fun fact_iter(n) { r = 1, while true do if n == 0 then return r else r = r * n, n = n - 1 }
                b = fact_iter(5)
                """);
        assertEquals(120, env.get("b"));
    }

    @Test
    void testComments() {
        Map<String, Integer> env = run("""
            // this is a comment
            x = 2  // inline comment
            y = x + 1
            """);
        assertEquals(2, env.get("x"));
        assertEquals(3, env.get("y"));
    }


    @Test
    void testIfWithoutElse() {
        // Condition is false and there is no else branch — variable should remain unchanged
        Map<String, Integer> env = run("""
            x = 5
            y = 0
            if x > 10 then y = 100
            """);
        assertEquals(5, env.get("x"));
        assertEquals(0, env.get("y"));
    }

    @Test
    void testNestedFunctions() {
        // A function that calls another function
        Map<String, Integer> env = run("""
            fun double(n) { return n * 2 }
            fun quadruple(n) { return double(double(n)) }
            result = quadruple(3)
            """);
        assertEquals(12, env.get("result"));
    }

    @Test
    void testNegativeArithmetic() {
        // Subtraction resulting in a negative value
        Map<String, Integer> env = run("""
            x = 3
            y = x - 10
            """);
        assertEquals(3, env.get("x"));
        assertEquals(-7, env.get("y"));
    }

    @Test
    void testWhileFalseFromStart() {
        // While condition is false from the start — body should never execute
        Map<String, Integer> env = run("""
            x = 5
            y = 0
            while x < 3 do y = 99
            """);
        assertEquals(5, env.get("x"));
        assertEquals(0, env.get("y"));
    }

    @Test
    void testMultipleAssignments() {
        // Same variable reassigned multiple times — only last value should remain
        Map<String, Integer> env = run("""
            x = 1
            x = 2
            x = 3
            """);
        assertEquals(3, env.get("x"));
    }

    @Test
    void testCommentOnly() {
        // Program with only comments — no variables should be produced
        Map<String, Integer> env = run("""
            // this program does nothing
            // just comments
            """);
        assertEquals(0, env.size());
    }
}