@echo off
setlocal enabledelayedexpansion

:: Check for admin rights
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo Please run this script as Administrator
    echo Right-click the script and select "Run as administrator"
    pause
    exit /b 1
)

:: Set up directories
set "INSTALL_DIR=%ProgramFiles%\AT++"
set "BUILD_DIR=%TEMP%\at_build"
set "CORE_DIR=%BUILD_DIR%\com\aspectplusplus\core"
set "COMPILER_DIR=%BUILD_DIR%\com\aspectplusplus\compiler"
set "SHELL_DIR=%BUILD_DIR%\com\aspectplusplus\tools\shell"
set "BIN_DIR=%INSTALL_DIR%\bin"
set "LIB_DIR=%INSTALL_DIR%\lib"
set "JRE_DIR=%INSTALL_DIR%\jre"
set "SECURITY_DIR=%INSTALL_DIR%\security"

echo Installing AT++ to: %INSTALL_DIR%

:: Create directories
echo Creating directories...
mkdir "%INSTALL_DIR%" 2>nul
mkdir "%BIN_DIR%" 2>nul
mkdir "%LIB_DIR%" 2>nul
mkdir "%JRE_DIR%" 2>nul
mkdir "%SECURITY_DIR%" 2>nul
mkdir "%BUILD_DIR%" 2>nul
mkdir "%CORE_DIR%" 2>nul
mkdir "%COMPILER_DIR%" 2>nul
mkdir "%SHELL_DIR%" 2>nul

:: Copy source files to build directory
echo Copying source files...
set "SRC_DIR=%~dp0"
copy "%SRC_DIR%TokenType.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%AspectModule.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%AspectToken.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%Lexer.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%AST.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%Parser.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%SemanticAnalyzer.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%CodeGenerator.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%AspectSymbol.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%CompilationContext.java" "%CORE_DIR%\" >nul
copy "%SRC_DIR%AspectCompilationException.java" "%CORE_DIR%\" >nul

:: Copy compiler and shell files
xcopy /s /i "%SRC_DIR%compiler\src\main\java\com\aspectplusplus\compiler\*.java" "%COMPILER_DIR%\" >nul
xcopy /s /i "%SRC_DIR%tools\src\main\java\com\aspectplusplus\tools\shell\*.java" "%SHELL_DIR%\" >nul

:: Create icon using existing SVG
echo Creating icon...
copy "%SRC_DIR%tools\src\main\resources\images\aspect-logo.svg" "%INSTALL_DIR%\at.ico" >nul 2>&1

:: Create security configuration
echo Creating security configuration...
(
    echo # AT++ Security Configuration
    echo security.level=STANDARD
    echo security.allow_network=false
    echo security.allow_file_access=true
    echo security.allowed_paths=%USERPROFILE%\Documents,%AT++_HOME%
    echo security.signature_verification=true
) > "%SECURITY_DIR%\config.properties"

:: Compile all Java files
echo Compiling...
cd /d "%BUILD_DIR%"
javac -d "%INSTALL_DIR%" com\aspectplusplus\core\*.java com\aspectplusplus\compiler\*.java com\aspectplusplus\tools\shell\*.java

if errorlevel 1 (
    echo Compilation failed
    cd /d "%SRC_DIR%"
    rmdir /s /q "%BUILD_DIR%"
    pause
    exit /b 1
)

:: Create AT++ launcher script
echo Creating launcher...
(
    echo @echo off
    echo setlocal
    echo set "AT++_HOME=%INSTALL_DIR%"
    echo set "AT++_SECURITY=STANDARD"
    echo set "AT++_ALLOWED_PATHS=%%USERPROFILE%%\Documents,%%AT++_HOME%%"
    echo.
    echo if "%%1"=="--security" (
    echo     set "AT++_SECURITY=%%2"
    echo     shift
    echo     shift
    echo ^)
    echo.
    echo if "%%1"=="--allow-network" (
    echo     set "AT++_NETWORK=true"
    echo     shift
    echo ^)
    echo.
    echo if "%%1"=="" (
    echo     java -cp "%%AT++_HOME%%" -DAT++_HOME="%%AT++_HOME%%" -DAT++_SECURITY="%%AT++_SECURITY%%" com.aspectplusplus.tools.shell.AspectLauncher
    echo ^) else (
    echo     java -cp "%%AT++_HOME%%" -DAT++_HOME="%%AT++_HOME%%" -DAT++_SECURITY="%%AT++_SECURITY%%" com.aspectplusplus.tools.shell.AspectLauncher "%%*"
    echo ^)
) > "%BIN_DIR%\at++.bat"

:: Create shell script without .bat extension
copy "%BIN_DIR%\at++.bat" "%BIN_DIR%\at++" >nul

:: Set environment variables
echo Setting environment variables...
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v AT++_HOME /t REG_SZ /d "%INSTALL_DIR%" /f
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v AT++_SECURITY /t REG_SZ /d "STANDARD" /f

:: Create file associations
echo Setting up file associations...
assoc .at=ATppFile >nul 2>&1
ftype ATppFile="\"%BIN_DIR%\at++.bat\" \"%%1\"" >nul 2>&1
reg add "HKCR\ATppFile\DefaultIcon" /ve /d "\"%INSTALL_DIR%\at.ico\"" /f >nul 2>&1

:: Clean up build directory
cd /d "%SRC_DIR%"
rmdir /s /q "%BUILD_DIR%"

echo.
echo AT++ installed successfully!
echo.
echo Environment Variables:
echo - AT++_HOME: %INSTALL_DIR%
echo - AT++_SECURITY: STANDARD
echo.
echo Security Features:
echo - Default security level: STANDARD
echo - Network access: Disabled by default
echo - File access: Limited to Documents and AT++_HOME
echo - Signature verification: Enabled
echo.
echo You can now:
echo - Run 'at++' to start the shell
echo - Run 'at++ file.at' to execute AT++ files
echo - Use 'at++ --security strict file.at' for enhanced security
echo - Use 'at++ --allow-network file.at' to enable network access
echo.
echo For more information, see README.md
echo.
pause