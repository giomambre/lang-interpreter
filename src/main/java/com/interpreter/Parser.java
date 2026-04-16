package com.interpreter;

import com.interpreter.ast.Expr;
import com.interpreter.ast.Stmt;
import com.interpreter.ast.Token;
import com.interpreter.ast.TokenType;

import java.util.List;
import java.util.ArrayList;
public class Parser {

    private final List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token advance() {
        Token t = tokens.get(pos);
        pos++;
        return t;
    }

    private Token expect(TokenType type) {
        Token t = peek();
        if (t.type() != type) {
            throw new RuntimeException("Expected " + type + " but got " + t);
        }
        return advance();
    }
    private Expr parsePrimary() {
        Token t = peek();

        if (t.type() == TokenType.NUMBER) {
            advance();
            return new Expr.Number(Integer.parseInt(t.value()));
        }

        if (t.type() == TokenType.TRUE) {
            advance();
            return new Expr.Number(1);
        }

        if (t.type() == TokenType.IDENTIFIER) {
            advance();
            // Function call
            if (peek().type() == TokenType.LPAREN) {
                expect(TokenType.LPAREN);
                List<Expr> args = new ArrayList<>();
                if (peek().type() != TokenType.RPAREN) {
                    args.add(parseExpression());
                    while (peek().type() == TokenType.COMMA) {
                        advance();
                        args.add(parseExpression());
                    }
                }
                expect(TokenType.RPAREN);
                return new Expr.Call(t.value(), args);
            }
            return new Expr.Variable(t.value());
        }

        if (t.type() == TokenType.LPAREN) {
            advance();
            Expr expr = parseExpression();
            expect(TokenType.RPAREN);
            return expr;
        }

        throw new RuntimeException("Unexpected token in expression: " + t);
    }

    private Expr parseTerm() {
        Expr left = parsePrimary();
        while (peek().type() == TokenType.STAR || peek().type() == TokenType.SLASH) {
            Token op = advance();
            Expr right = parsePrimary();
            left = new Expr.BinaryOp(left, op, right);
        }
        return left;
    }

    private Expr parseExpression() {
        Expr left = parseTerm();
        while (peek().type() == TokenType.PLUS || peek().type() == TokenType.MINUS) {
            Token op = advance();
            Expr right = parseTerm();
            left = new Expr.BinaryOp(left, op, right);
        }
        return left;
    }

    private Expr parseComparison() {
        Expr left = parseExpression();
        while (peek().type() == TokenType.LESS ||
                peek().type() == TokenType.LESS_EQUAL ||
                peek().type() == TokenType.GREATER ||
                peek().type() == TokenType.GREATER_EQUAL ||
                peek().type() == TokenType.EQUAL_EQUAL) {
            Token op = advance();
            Expr right = parseExpression();
            left = new Expr.BinaryOp(left, op, right);
        }
        return left;
    }


    private Stmt parseStatement() {
        Token t = peek();

        // Function definition: fun name(params) { body }
        if (t.type() == TokenType.FUN) {
            advance();
            String name = expect(TokenType.IDENTIFIER).value();
            expect(TokenType.LPAREN);
            List<String> params = new ArrayList<>();
            if (peek().type() != TokenType.RPAREN) {
                params.add(expect(TokenType.IDENTIFIER).value());
                while (peek().type() == TokenType.COMMA) {
                    advance();
                    params.add(expect(TokenType.IDENTIFIER).value());
                }
            }
            expect(TokenType.RPAREN);
            expect(TokenType.LBRACE);
            Stmt body = parseBody();
            expect(TokenType.RBRACE);
            return new Stmt.FunDef(name, params, body);
        }

        // If: if condition then stmt else stmt
        if (t.type() == TokenType.IF) {
            advance();
            Expr condition = parseComparison();
            expect(TokenType.THEN);
            Stmt thenBranch = parseStatement();
            Stmt elseBranch = null;
            if (peek().type() == TokenType.ELSE) {
                advance();
                elseBranch = parseStatement();
            }
            return new Stmt.If(condition, thenBranch, elseBranch);
        }

        // While: while condition do stmt
        if (t.type() == TokenType.WHILE) {
            advance();
            Expr condition = parseComparison();
            expect(TokenType.DO);
            Stmt body = parseBody();
            return new Stmt.While(condition, body);
        }

        // Return: return expr
        if (t.type() == TokenType.RETURN) {
            advance();
            Expr value = parseComparison();
            return new Stmt.Return(value);
        }

        // Assignment: name = expr
        if (t.type() == TokenType.IDENTIFIER) {
            advance();
            expect(TokenType.ASSIGN);
            Expr value = parseComparison();
            return new Stmt.Assign(t.value(), value);
        }

        throw new RuntimeException("Unexpected token in statement: " + t);
    }

    // Parses a sequence of statements separated by commas (used inside { })
    private Stmt parseBody() {
        List<Stmt> stmts = new ArrayList<>();
        stmts.add(parseStatement());
        while (peek().type() == TokenType.COMMA) {
            advance();
            stmts.add(parseStatement());
        }
        return stmts.size() == 1 ? stmts.get(0) : new Stmt.Sequence(stmts);
    }

    public List<Stmt> parse() {
        List<Stmt> stmts = new ArrayList<>();
        while (peek().type() != TokenType.EOF) {
            stmts.add(parseStatement());
        }
        return stmts;
    }
}