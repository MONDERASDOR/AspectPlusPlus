package com.aspectplusplus.tools.shell;

import com.aspectplusplus.core.*;
import java.io.*;
import java.nio.file.*;

public class AspectLauncher {
    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        if (args.length == 0) {
            startShell();
            return;
        }

        String command = args[0];
        switch (command) {
            case "--version" -> System.out.println("AT++ version " + VERSION);
            case "--help" -> showHelp();
            default -> {
                if (command.startsWith("-")) {
                    System.err.println("Unknown option: " + command);
                    System.err.println("Use --help for usage information");
                    System.exit(1);
                }
                executeFile(command);
            }
        }
    }

    private static void showHelp() {
        System.out.println("AT++ Programming Language");
        System.out.println("Usage: at++ [options] [file]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --help     Show this help message");
        System.out.println("  --version  Show version information");
        System.out.println();
        System.out.println("If no file is specified, starts the AT++ shell");
    }

    private static void startShell() {
        AspectShell shell = new AspectShell();
        shell.run();
    }

    private static void executeFile(String filename) {
        try {
            String sourceCode = Files.readString(Path.of(filename));
            AspectModule module = new AspectModule("main");
            module.compile(sourceCode);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (AspectCompilationException e) {
            System.err.println("Compilation error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
} 