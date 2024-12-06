package com.aspectplusplus.tools.fileassoc;

import java.nio.file.*;
import java.util.*;

public class FileAssociationManager {
    private static final String FILE_EXTENSION = ".at";
    private static final String ICON_RESOURCE = "/resources/aspect.ico";
    
    public static void registerFileAssociation() {
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            if (os.contains("windows")) {
                registerWindowsAssociation();
            } else if (os.contains("mac")) {
                registerMacAssociation();
            } else if (os.contains("linux")) {
                registerLinuxAssociation();
            }
        } catch (Exception e) {
            System.err.println("Failed to register file association: " + e.getMessage());
        }
    }

    private static void registerWindowsAssociation() {
        // Using Windows Registry
        ProcessBuilder pb = new ProcessBuilder(
            "reg", "add", "HKCR\\.at", "/ve", "/d", "AspectPlusPlusFile", "/f"
        );
        pb.start().waitFor();

        // Register icon
        pb = new ProcessBuilder(
            "reg", "add", "HKCR\\AspectPlusPlusFile\\DefaultIcon", 
            "/ve", "/d", getIconPath(), "/f"
        );
        pb.start().waitFor();

        // Register open command
        String command = "\"%ASPECT_HOME%\\bin\\aspect++.exe\" \"%1\"";
        pb = new ProcessBuilder(
            "reg", "add", "HKCR\\AspectPlusPlusFile\\shell\\open\\command",
            "/ve", "/d", command, "/f"
        );
        pb.start().waitFor();
    }

    private static void registerMacAssociation() {
        // Create Info.plist entry
        Path plist = Paths.get(System.getProperty("user.home"), 
            "Library/Preferences/com.aspectplusplus.plist");
        
        // Write plist content
        String plistContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
            <plist version="1.0">
            <dict>
                <key>CFBundleDocumentTypes</key>
                <array>
                    <dict>
                        <key>CFBundleTypeExtensions</key>
                        <array>
                            <string>at</string>
                        </array>
                        <key>CFBundleTypeIconFile</key>
                        <string>aspect.icns</string>
                        <key>CFBundleTypeName</key>
                        <string>Aspect++ Source File</string>
                        <key>CFBundleTypeRole</key>
                        <string>Editor</string>
                    </dict>
                </array>
            </dict>
            </plist>
            """;
        Files.writeString(plist, plistContent);
    }

    private static void registerLinuxAssociation() {
        // Create .desktop file
        Path desktop = Paths.get(System.getProperty("user.home"), 
            ".local/share/applications/aspectplusplus.desktop");
            
        String desktopContent = """
            [Desktop Entry]
            Type=Application
            Name=Aspect++
            Exec=aspect++ %f
            Terminal=false
            Categories=Development;
            MimeType=text/x-aspect;
            Icon=/usr/share/icons/aspectplusplus/aspect.png
            """;
        Files.writeString(desktop, desktopContent);

        // Create mime type
        Path mime = Paths.get("/usr/share/mime/packages/aspectplusplus.xml");
        String mimeContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <mime-info xmlns="http://www.freedesktop.org/standards/shared-mime-info">
                <mime-type type="text/x-aspect">
                    <comment>Aspect++ source file</comment>
                    <glob pattern="*.at"/>
                </mime-type>
            </mime-info>
            """;
        Files.writeString(mime, mimeContent);
    }
} 