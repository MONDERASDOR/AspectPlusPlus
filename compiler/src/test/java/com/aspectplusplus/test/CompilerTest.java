package com.aspectplusplus.test;

import com.aspectplusplus.compiler.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {
    @Test
    public void testBasicProgram() {
        String sourceCode = """
            // This is a basic Aspect++ program
            fun main() {
                val message = "Hello, Aspect++"
                println(message)
                
                // Testing number operations
                val x = 10
                val y = 20
                println("Sum: ${x + y}")
                
                // Testing null safety
                val nullableValue: String? = null
                val length = nullableValue?.length ?: 0
                println("Length: $length")
            }
            """;

        AspectModule module = new AspectModule("test");
        try {
            module.compile(sourceCode);
            assertTrue(true, "Compilation successful");
        } catch (AspectCompilationException e) {
            fail("Compilation failed: " + e.getMessage());
        }
    }
} 