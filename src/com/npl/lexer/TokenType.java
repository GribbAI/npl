package com.npl.lexer;

public enum TokenType {
    // ключевые слова
    PRINT, IF, ELSE, WHILE,
    // операторы и разделители
    PLUS, MINUS, MULT, DIV,
    ASSIGN, SEMICOLON,
    LPAREN, RPAREN, LBRACE, RBRACE,
    EQ, NEQ, LT, GT, LEQ, GEQ,
    // инднтификаторы и литералы
    IDENTIFIER, NUMBER, STRING,
    EOF, UNKNOWN
}