package com.interpreter.ast;

public enum TokenType {
    // Literals
    NUMBER, IDENTIFIER,

    // Arithmetic operators
    PLUS, MINUS, STAR, SLASH,

    // Comparison operators
    LESS, LESS_EQUAL, GREATER, GREATER_EQUAL, EQUAL_EQUAL,

    // Punctuation
    ASSIGN, LPAREN, RPAREN, LBRACE, RBRACE, COMMA,

    // Keywords
    IF, THEN, ELSE, WHILE, DO, FUN, RETURN, TRUE,

    // Special
    EOF
}