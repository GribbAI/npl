package com.npl;

public class Main {
    public static void main(String[] args) {
        String code = "for (i = 0; i < 10; i++) { \nprint(i);\n}";
        
        String result = NPL.interpret(code);
        System.out.println("код:\n"+ code + "\n\nрезультат:");
        System.out.println(result);
    }
}