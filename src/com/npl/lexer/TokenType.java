package com.npl.lexer;

public enum TokenType {
    // ключевые слова
    PRINT, IF, ELSE, WHILE, FOR, END,
    // операторы и разделители
    PLUS, MINUS, MULT, DIV, PLUS_ASSIGN, 
    MINUS_ASSIGN, MULT_ASSIGN, DIV_ASSIGN,
    INCREMENT, DECREMENT, ASSIGN, SEMICOLON,
    LPAREN, RPAREN, LBRACE, RBRACE,
    EQ, NEQ, LT, GT, LEQ, GEQ, COMMA,
    OR, AND,
    // инднтификаторы и литералы
    IDENTIFIER, NUMBER, STRING,
    EOF, UNKNOWN, TRUE, FALSE, NONE
}