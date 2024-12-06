package com.aspectplusplus.runtime.interop;

import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.Path;

public class NativeInterop {
    private static final ConcurrentHashMap<String, NativeLibrary> loadedLibraries = new ConcurrentHashMap<>();
    
    public static class NativeLibrary {
        private final String name;
        private final long handle;
        private final Map<String, Long> functionCache;
        
        private NativeLibrary(String name, long handle) {
            this.name = name;
            this.handle = handle;
            this.functionCache = new ConcurrentHashMap<>();
        }
        
        public native <T> T invokeFunction(String name, Object... args);
        public native void unload();
        
        protected void finalize() {
            unload();
        }
    }
    
    public static NativeLibrary loadLibrary(Path path) {
        String absolutePath = path.toAbsolutePath().toString();
        return loadedLibraries.computeIfAbsent(absolutePath, k -> {
            long handle = loadNativeLibrary(k);
            return new NativeLibrary(k, handle);
        });
    }
    
    private static native long loadNativeLibrary(String path);
} 