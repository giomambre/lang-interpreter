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
}