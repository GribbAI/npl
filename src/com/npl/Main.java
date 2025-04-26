package com.npl;

public class Main {
    public static void main(String[] args) {
        String code = "print('hi');";
        
        String result = NPL.interpret(code);
        System.out.println("код:\n"+ code + "\n\nрезультат:");
        System.out.println(result);
    }
}