package com.npl.ast;
import com.npl.lexer.TokenType;

public class PostfixExpression extends Expression {
    public final Expression expression;
    public final TokenType operator;

    public PostfixExpression(Expression expression, TokenType operator) {
        this.expression = expression;
        this.operator = operator;
    }
}