package com.npl.parser;

import com.npl.lexer.Lexer;
import com.npl.lexer.Token;
import com.npl.lexer.TokenType;
import com.npl.ast.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        currentToken = lexer.getNextToken();
    }

    private void eat(TokenType type) {
        if (currentToken.type == type) {
            currentToken = lexer.getNextToken();
        } else {
            throw new RuntimeException("Expected token " + type + " but got " + currentToken.type);
        }
    }

    public Program parseProgram() {
        List<Statement> statements = new ArrayList<>();
        while (currentToken.type != TokenType.EOF) {
            statements.add(parseStatement());
        }
        return new Program(statements);
    }

    private Statement parseStatement() {
        if (currentToken.type == TokenType.PRINT) {
            return parsePrintStatement();
        } else if (currentToken.type == TokenType.IF) {
            return parseIfStatement();
        } else if (currentToken.type == TokenType.WHILE) {
            return parseWhileStatement();
        } else if (currentToken.type == TokenType.IDENTIFIER) {
            return parseAssignmentStatement();
        }
        throw new RuntimeException("Unknown statement starting with token: " + currentToken);
    }

    private Statement parsePrintStatement() {
        eat(TokenType.PRINT);
        eat(TokenType.LPAREN);
        Expression expr = parseExpression();
        eat(TokenType.RPAREN);
        eat(TokenType.SEMICOLON);
        return new PrintStatement(expr);
    }

    private Statement parseAssignmentStatement() {
        String varName = currentToken.text;
        eat(TokenType.IDENTIFIER);
        eat(TokenType.ASSIGN);
        Expression expr = parseExpression();
        eat(TokenType.SEMICOLON);
        return new AssignmentStatement(varName, expr);
    }

    private Statement parseIfStatement() {
        eat(TokenType.IF);
        eat(TokenType.LPAREN);
        Expression condition = parseExpression();
        eat(TokenType.RPAREN);
        eat(TokenType.LBRACE);
        List<Statement> thenBranch = new ArrayList<>();
        while (currentToken.type != TokenType.RBRACE) {
            thenBranch.add(parseStatement());
        }
        eat(TokenType.RBRACE);

        List<Statement> elseBranch = null;
        if (currentToken.type == TokenType.ELSE) {
            eat(TokenType.ELSE);
            eat(TokenType.LBRACE);
            elseBranch = new ArrayList<>();
            while (currentToken.type != TokenType.RBRACE) {
                elseBranch.add(parseStatement());
            }
            eat(TokenType.RBRACE);
        }
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement parseWhileStatement() {
        eat(TokenType.WHILE);
        eat(TokenType.LPAREN);
        Expression condition = parseExpression();
        eat(TokenType.RPAREN);
        eat(TokenType.LBRACE);
        List<Statement> body = new ArrayList<>();
        while (currentToken.type != TokenType.RBRACE) {
            body.add(parseStatement());
        }
        eat(TokenType.RBRACE);
        return new WhileStatement(condition, body);
    }

    private Expression parseExpression() {
        return parseEquality();
    }

    private Expression parseEquality() {
        Expression expr = parseRelational();
        while (currentToken.type == TokenType.EQ || currentToken.type == TokenType.NEQ) {
            String op = currentToken.text;
            if (currentToken.type == TokenType.EQ) eat(TokenType.EQ);
            else eat(TokenType.NEQ);
            Expression right = parseRelational();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }

    private Expression parseRelational() {
        Expression expr = parseTerm();
        while (currentToken.type == TokenType.LT || currentToken.type == TokenType.GT ||
               currentToken.type == TokenType.LEQ || currentToken.type == TokenType.GEQ) {
            String op = currentToken.text;
            if (currentToken.type == TokenType.LT) eat(TokenType.LT);
            else if (currentToken.type == TokenType.GT) eat(TokenType.GT);
            else if (currentToken.type == TokenType.LEQ) eat(TokenType.LEQ);
            else if (currentToken.type == TokenType.GEQ) eat(TokenType.GEQ);
            Expression right = parseTerm();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }

    private Expression parseTerm() {
        Expression expr = parseFactor();
        while (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS) {
            String op = currentToken.text;
            if (currentToken.type == TokenType.PLUS) eat(TokenType.PLUS);
            else eat(TokenType.MINUS);
            Expression right = parseFactor();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }

    private Expression parseFactor() {
        Expression expr = parsePrimary();
        while (currentToken.type == TokenType.MULT || currentToken.type == TokenType.DIV) {
            String op = currentToken.text;
            if (currentToken.type == TokenType.MULT) eat(TokenType.MULT);
            else eat(TokenType.DIV);
            Expression right = parsePrimary();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }

    private Expression parsePrimary() {
        if (currentToken.type == TokenType.STRING) {
            String str = currentToken.text;
            eat(TokenType.STRING);
            return new StringExpression(str);
        } else if (currentToken.type == TokenType.NUMBER) {
            double value = Double.parseDouble(currentToken.text);
            eat(TokenType.NUMBER);
            return new NumberExpression(value);
        } else if (currentToken.type == TokenType.IDENTIFIER) {
            String name = currentToken.text;
            eat(TokenType.IDENTIFIER);
            return new VariableExpression(name);
        } else if (currentToken.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN);
            Expression expr = parseExpression();
            eat(TokenType.RPAREN);
            return expr;
        }
        throw new RuntimeException("Unexpected token in expression: " + currentToken);
    }
}