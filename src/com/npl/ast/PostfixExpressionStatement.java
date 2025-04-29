// PostfixExpressionStatement.java
package com.npl.ast;

import com.npl.lexer.TokenType;

public class PostfixExpressionStatement extends Statement {
    private final String varName;
    private final TokenType op;

    public PostfixExpressionStatement(String varName, TokenType op) {
        this.varName = varName;
        this.op = op;
    }

    public String getVarName() {
        return varName;
    }

    public TokenType getOp() {
        return op;
    }

    @Override
    public String toString() {
        return varName + (op == TokenType.INCREMENT ? "++" : "--");
    }
}