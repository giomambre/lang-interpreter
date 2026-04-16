package com.interpreter;

import com.interpreter.ast.Stmt;
import com.interpreter.ast.Token;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        // Read entire program from stdin
        Scanner scanner = new Scanner(System.in);
        StringBuilder source = new StringBuilder();
        while (scanner.hasNextLine()) {
            source.append(scanner.nextLine()).append("\n");
        }

        // Lexer → Parser → Interpreter pipeline
        Lexer lexer = new Lexer(source.toString());
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        List<Stmt> stmts = parser.parse();

        Interpreter interpreter = new Interpreter();
        Map<String, Integer> result = interpreter.interpret(stmts);

        // Print variables in alphabetical order, excluding function definitions
        new TreeMap<>(result).forEach((name, value) ->
                System.out.println(name + ": " + value)
        );
    }
}