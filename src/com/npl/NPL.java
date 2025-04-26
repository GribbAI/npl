package com.npl;

import com.npl.lexer.Lexer;
import com.npl.parser.Parser;
import com.npl.ast.Program;
import com.npl.interpreter.InterpreterEngine;

public class NPL {
    public static String interpret(String code) {
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        InterpreterEngine interpreter = new InterpreterEngine();
        interpreter.interpret(program);
        return interpreter.getOutput();
    }
}