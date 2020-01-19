package com.integwise.procmon;

public class App {
    public static void main(String[] args) {
        if (args != null && args.length < 1) {
            throw new IllegalArgumentException("Please pass input file name");
        }

        ProcessOrchestrator po = new ProcessOrchestrator(args[0]);
        po.start();
    }
}
