package com.interpreter;

import com.interpreter.ast.Stmt;
import com.interpreter.ast.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorHandlingTest {

    // helper: runs the full pipeline
    private void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        List<Stmt> stmts = parser.parse();
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(stmts);
    }

    @Test
    void testUnexpectedTokenInParser() {
        // "x =" with no value — Parser should throw
        assertThrows(RuntimeException.class, () -> run("x ="));
    }

    @Test
    void testUndefinedVariable() {
        // "y = x + 1" where x is never defined — Interpreter should throw
        assertThrows(RuntimeException.class, () -> run("y = x + 1"));
    }

    @Test
    void testUndefinedFunction() {
        // Calling a function that was never defined — Interpreter should throw
        assertThrows(RuntimeException.class, () -> run("result = foo(1, 2)"));
    }

    @Test
    void testUnrecognizedCharacter() {
        // "@" is not a valid character — Lexer should throw
        assertThrows(RuntimeException.class, () -> run("x = 2 @ 3"));
    }
}