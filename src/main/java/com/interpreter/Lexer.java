package com.interpreter;

import com.interpreter.ast.Token;
import com.interpreter.ast.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lexer {

    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
            Map.entry("if", TokenType.IF),
            Map.entry("then", TokenType.THEN),
            Map.entry("else", TokenType.ELSE),
            Map.entry("while", TokenType.WHILE),
            Map.entry("do", TokenType.DO),
            Map.entry("fun", TokenType.FUN),
            Map.entry("return", TokenType.RETURN),
            Map.entry("true", TokenType.TRUE)
    );

    private final String source;
    private int pos;

    public Lexer(String source) {
        this.source = source;
        this.pos = 0;
    }

    private char peek() {
        if (pos >= source.length()) return '\0';
        return source.charAt(pos);
    }

    private char peekNext() {
        if (pos + 1 >= source.length()) return '\0';
        return source.charAt(pos + 1);
    }

    private char advance() {
        return source.charAt(pos++);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < source.length()) {
            char c = peek();

            // Skip whitespace and newlines
            if (Character.isWhitespace(c)) {
                advance();
                continue;
            }

            // Numbers
            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (pos < source.length() && Character.isDigit(peek())) {
                    sb.append(advance());
                }
                tokens.add(new Token(TokenType.NUMBER, sb.toString()));
                continue;
            }

            // Identifiers and keywords
            if (Character.isLetter(c) || c == '_') {
                StringBuilder sb = new StringBuilder();
                while (pos < source.length() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
                    sb.append(advance());
                }
                String word = sb.toString();
                TokenType type = KEYWORDS.getOrDefault(word, TokenType.IDENTIFIER);
                tokens.add(new Token(type, type == TokenType.IDENTIFIER ? word : null));
                continue;
            }

            // Operators and punctuation
            advance(); // consume the character
            switch (c) {
                case '+' -> tokens.add(new Token(TokenType.PLUS, null));
                case '-' -> tokens.add(new Token(TokenType.MINUS, null));
                case '*' -> tokens.add(new Token(TokenType.STAR, null));
                case '/' -> {
                    // check for single-line comment '//'
                    if (peek() == '/') {
                        while (pos < source.length() && peek() != '\n') advance();
                    } else {
                        tokens.add(new Token(TokenType.SLASH, null));
                    }
                }                case '(' -> tokens.add(new Token(TokenType.LPAREN, null));
                case ')' -> tokens.add(new Token(TokenType.RPAREN, null));
                case '{' -> tokens.add(new Token(TokenType.LBRACE, null));
                case '}' -> tokens.add(new Token(TokenType.RBRACE, null));
                case ',' -> tokens.add(new Token(TokenType.COMMA, null));
                case '=' -> {
                    if (peek() == '=') { advance(); tokens.add(new Token(TokenType.EQUAL_EQUAL, null)); }
                    else tokens.add(new Token(TokenType.ASSIGN, null));
                }
                case '<' -> {
                    if (peek() == '=') { advance(); tokens.add(new Token(TokenType.LESS_EQUAL, null)); }
                    else tokens.add(new Token(TokenType.LESS, null));
                }
                case '>' -> {
                    if (peek() == '=') { advance(); tokens.add(new Token(TokenType.GREATER_EQUAL, null)); }
                    else tokens.add(new Token(TokenType.GREATER, null));
                }
                default -> throw new RuntimeException("Unexpected character: " + c);
            }
        }

        tokens.add(new Token(TokenType.EOF, null));
        return tokens;
    }
}