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
    private Token nextToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        currentToken = lexer.getNextToken();
        nextToken = lexer.getNextToken();
    }

    private void advance() {
        currentToken = nextToken;
        nextToken = lexer.getNextToken();
    }

    private void eat(TokenType type) {
        if (currentToken.type == type) {
            advance();
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
        switch (currentToken.type) {
            case PRINT:    return parsePrintStatement();
            case IF:       return parseIfStatement();
            case WHILE:    return parseWhileStatement();
            case FOR:      return parseForStatement();
            case IDENTIFIER:
                String name = currentToken.text;
                eat(TokenType.IDENTIFIER);
                if (currentToken.type == TokenType.ASSIGN) {
                    return parseAssignmentStatement(name);
                } else if (currentToken.type == TokenType.INCREMENT
                        || currentToken.type == TokenType.DECREMENT) {
                    return parsePostfixExpression(name);
                } else {
                    Expression expr = parseExpression();
                    eat(TokenType.SEMICOLON);
                    return new ExpressionStatement(expr);
                }
            default:
                throw new RuntimeException("Unknown statement starting with token: " + currentToken);
        }
    }

    private Statement parsePrintStatement() {
        eat(TokenType.PRINT);
        eat(TokenType.LPAREN);

        List<Expression> exprs = new ArrayList<>();
        exprs.add(parseExpression());

        Expression end = new StringExpression("\n");
        while (currentToken.type == TokenType.COMMA) {
            eat(TokenType.COMMA);
            if (currentToken.type == TokenType.END) {
                eat(TokenType.END);
                eat(TokenType.ASSIGN);
                String endText = currentToken.text;
                eat(TokenType.STRING);
                end = new StringExpression(endText);
                break;
            } else {
                exprs.add(parseExpression());
            }
        }

        eat(TokenType.RPAREN);
        eat(TokenType.SEMICOLON);

        return new PrintStatement(exprs, end);
    }

    private Statement parseAssignmentStatement(String varName) {
        eat(TokenType.ASSIGN);
        Expression expr = parseExpression();
        eat(TokenType.SEMICOLON);
        return new AssignmentStatement(varName, expr);
    }

    private Statement parsePostfixExpression(String varName) {
        TokenType op = currentToken.type;
        eat(op);
        eat(TokenType.SEMICOLON);
        return new PostfixExpressionStatement(varName, op);
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

    private Statement parseForStatement() {
        eat(TokenType.FOR);
        eat(TokenType.LPAREN);

        String initVar = currentToken.text;
        eat(TokenType.IDENTIFIER);
        eat(TokenType.ASSIGN);
        Expression initExpr = parseExpression();
        eat(TokenType.SEMICOLON);
        Statement initialization = new AssignmentStatement(initVar, initExpr);

        Expression condition = parseExpression();
        eat(TokenType.SEMICOLON);

        Statement update;
        String updVar = currentToken.text;
        eat(TokenType.IDENTIFIER);
        if (currentToken.type == TokenType.INCREMENT || currentToken.type == TokenType.DECREMENT) {
            TokenType op = currentToken.type;
            eat(op);
            update = new PostfixExpressionStatement(updVar, op);
        } else if (currentToken.type == TokenType.ASSIGN) {
            eat(TokenType.ASSIGN);
            Expression updExpr = parseExpression();
            update = new AssignmentStatement(updVar, updExpr);
        } else {
            throw new RuntimeException("Unexpected token in for-update: " + currentToken);
        }
        eat(TokenType.RPAREN);
        eat(TokenType.LBRACE);

        List<Statement> body = new ArrayList<>();
        while (currentToken.type != TokenType.RBRACE) {
            body.add(parseStatement());
        }
        eat(TokenType.RBRACE);

        return new ForStatement(initialization, condition, update, body);
    }

    // выражения
    private Expression parseExpression() {
        return parseOr();
    }

    private Expression parseOr() {
        Expression expr = parseAnd();
        while (currentToken.type == TokenType.OR) {
            String op = currentToken.text;
            eat(TokenType.OR);
            Expression right = parseAnd();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }

    private Expression parseAnd() {
        Expression expr = parseEquality();
        while (currentToken.type == TokenType.AND) {
            String op = currentToken.text;
            eat(TokenType.AND);
            Expression right = parseEquality();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
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
            else eat(TokenType.GEQ);
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
        switch (currentToken.type) {
            case STRING:
                String s = currentToken.text;
                eat(TokenType.STRING);
                return new StringExpression(s);
            case NUMBER:
                double v = Double.parseDouble(currentToken.text);
                eat(TokenType.NUMBER);
                return new NumberExpression(v);
            case IDENTIFIER:
                String name = currentToken.text;
                eat(TokenType.IDENTIFIER);
                return new VariableExpression(name);
            case TRUE:
                eat(TokenType.TRUE);
                return new BooleanExpression(true);
            case FALSE:
                eat(TokenType.FALSE);
                return new BooleanExpression(false);
            case NONE:
                eat(TokenType.NONE);
                return new NoneExpression();
            case LPAREN:
                eat(TokenType.LPAREN);
                Expression inner = parseExpression();
                eat(TokenType.RPAREN);
                return inner;
            default:
                throw new RuntimeException("Unexpected token in expression: " + currentToken);
        }
    }
}