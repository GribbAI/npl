package com.npl.lexer;

public class Lexer {
    private final String input;
    private int pos;
    private char currentChar;

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
        this.currentChar = input.length() > 0 ? input.charAt(0) : '\0';
    }

    private void advance() {
        pos++;
        currentChar = pos < input.length() ? input.charAt(pos) : '\0';
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private String number() {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(currentChar)) {
            sb.append(currentChar);
            advance();
        }
        if (currentChar == '.') {
            sb.append('.');
            advance();
            while (Character.isDigit(currentChar)) {
                sb.append(currentChar);
                advance();
            }
        }
        return sb.toString();
    }

    private String identifier() {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            sb.append(currentChar);
            advance();
        }
        return sb.toString();
    }

    private String stringLiteral(char quoteChar) {
        StringBuilder sb = new StringBuilder();
        advance();
        while (currentChar != '\0' && currentChar != quoteChar) {
            if (currentChar == '\\') {
                advance();
                switch (currentChar) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case '"': sb.append('"'); break;
                    case '\'': sb.append('\''); break;
                    case '\\': sb.append('\\'); break;
                    default: sb.append(currentChar); break;
                }
            } else {
                sb.append(currentChar);
            }
            advance();
        }
        advance();
        return sb.toString();
    }

    public Token getNextToken() {
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            if (currentChar == '"' || currentChar == '\'') {
                char quote = currentChar;
                String str = stringLiteral(quote);
                return new Token(TokenType.STRING, str);
            }

            if (Character.isDigit(currentChar)) {
                return new Token(TokenType.NUMBER, number());
            }

            if (Character.isLetter(currentChar) || currentChar == '_') {
                String id = identifier();
                switch (id) {
                    case "print": return new Token(TokenType.PRINT, id);
                    case "if": return new Token(TokenType.IF, id);
                    case "else": return new Token(TokenType.ELSE, id);
                    case "while": return new Token(TokenType.WHILE, id);
                    default: return new Token(TokenType.IDENTIFIER, id);
                }
            }

            switch (currentChar) {
                case '(': advance(); return new Token(TokenType.LPAREN, "(");
                case ')': advance(); return new Token(TokenType.RPAREN, ")");
                case '{': advance(); return new Token(TokenType.LBRACE, "{");
                case '}': advance(); return new Token(TokenType.RBRACE, "}");
                case ';': advance(); return new Token(TokenType.SEMICOLON, ";");
                case '+': advance(); return new Token(TokenType.PLUS, "+");
                case '-': advance(); return new Token(TokenType.MINUS, "-");
                case '*': advance(); return new Token(TokenType.MULT, "*");
                case '/': advance(); return new Token(TokenType.DIV, "/");
                case '=':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        return new Token(TokenType.EQ, "==");
                    }
                    return new Token(TokenType.ASSIGN, "=");
                case '!':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        return new Token(TokenType.NEQ, "!=");
                    }
                    break;
                case '<':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        return new Token(TokenType.LEQ, "<=");
                    }
                    return new Token(TokenType.LT, "<");
                case '>':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        return new Token(TokenType.GEQ, ">=");
                    }
                    return new Token(TokenType.GT, ">");
            }

            throw new RuntimeException("Unexpected character: " + currentChar);
        }

        return new Token(TokenType.EOF, "");
    }
}