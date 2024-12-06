package com.aspectplusplus.tools.shell;

import com.aspectplusplus.core.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AspectShell {
    private static final String PROMPT = "at++> ";
    private final Scanner scanner;
    private final AspectModule module;
    private boolean running;

    public AspectShell() {
        this.scanner = new Scanner(System.in);
        this.module = new AspectModule("shell");
        this.running = true;
    }

    public void run() {
        showWelcome();
        
        while (running) {
            System.out.print(PROMPT);
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            try {
                handleCommand(input);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleCommand(String input) throws Exception {
        switch (input) {
            case "exit", "quit" -> {
                running = false;
                return;
            }
            case "help" -> showHelp();
            case "version" -> System.out.println("AT++ version 1.0.0");
            case "clear" -> clearScreen();
            default -> {
                if (input.startsWith("run ")) {
                    runFile(input.substring(4).trim());
                } else {
                    // Treat as AT++ code
                    module.compile(input);
                }
            }
        }
    }

    private void showWelcome() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║           AT++ Shell v1.0.0        ║");
        System.out.println("║ Type 'help' for available commands ║");
        System.out.println("║ Type 'exit' to quit               ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println();
    }

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("run <file>     - Run an AT++ source file");
        System.out.println("help          - Show this help message");
        System.out.println("version       - Show version information");
        System.out.println("clear         - Clear the screen");
        System.out.println("exit          - Exit the shell");
        System.out.println();
    }

    private void runFile(String filename) throws Exception {
        String sourceCode = Files.readString(Path.of(filename));
        module.compile(sourceCode);
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
} 