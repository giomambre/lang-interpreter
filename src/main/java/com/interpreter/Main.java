package com.interpreter;

import com.interpreter.ast.Stmt;
import com.interpreter.ast.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String source = """
                x = 0
                y = 0
                while x < 3 do if x == 1 then y = 10 else y = y + 1, x = x + 1
                """;

        // Lexer
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        System.out.println("=== TOKENS ===");
        for (Token t : tokens) {
            System.out.println(t);
        }


        //Parser
        Parser parser = new Parser(tokens);
        List<Stmt> stmts = parser.parse();

        System.out.println("\n=== AST ===");
        for (Stmt s : stmts) {
            System.out.println(s);
        }
    }
}