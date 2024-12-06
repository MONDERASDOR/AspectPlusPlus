# AT++ Programming Language

AT++ is a modern, aspect-oriented programming language that combines the power of aspect-oriented programming with modern language features.


# My PATH was deleted in the procces . so please Don't forgot to support me

## Table of Contents
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Installation Steps](#installation-steps)
  - [Verifying Installation](#verifying-installation)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Environment Variables](#environment-variables)
  - [Security Warnings](#security-warnings)
- [Quick Start](#quick-start)
- [Development Tools](#development-tools)

## Installation

### Prerequisites
- Windows 10 or later
- Java JDK 17 or later (recommended: JDK 23)
- Administrator privileges for installation

### Installation Steps

1. **Download AT++**
   - Download the latest release from the official repository
   - Extract the files to a temporary location

2. **Run Installation**
   ```powershell
   # Open PowerShell as Administrator
   Start-Process -FilePath "run.bat" -Verb RunAs
   ```

3. **Environment Setup**
   - The installer will create AT++_HOME environment variable
   - No manual PATH configuration needed
   - Default installation directory: `C:\Program Files\AT++`

### Verifying Installation

Test your installation by running:
```cmd
at++ --version
```

You should see:
```
AT++ version 1.0.0
```

## Troubleshooting

### Common Issues

1. **Command Not Found**
   ```
   'at++' is not recognized...
   ```
   Solution:
   - Restart your terminal/PowerShell
   - Verify AT++_HOME is set: `echo %AT++_HOME%`
   - Run the installer again if needed

2. **Permission Denied**
   ```
   Access denied...
   ```
   Solution:
   - Right-click run.bat
   - Select "Run as administrator"
   - Check Windows Defender settings

3. **Java Issues**
   ```
   'java' is not recognized...
   ```
   Solution:
   - AT++ uses its own Java runtime
   - No system Java required
   - Check AT++_HOME/jre directory

### Environment Variables

AT++ uses the following environment variables:

- `AT++_HOME`: Main installation directory
  ```cmd
  # Check if set correctly
  echo %AT++_HOME%
  
  # Should show
  C:\Program Files\AT++
  ```

- `AT++_SECURITY`: Security settings
  ```cmd
  # Default security level
  echo %AT++_SECURITY%
  
  # Should show
  STANDARD
  ```

### Security Warnings

AT++ includes several security features:

1. **Execution Policies**
   - STANDARD: Default, allows local file execution
   - STRICT: Requires signed code
   - DEVELOPMENT: Allows all operations

2. **File Access**
   - By default, AT++ can only access:
     - Current working directory
     - AT++_HOME directory
     - User's documents folder

3. **Network Access**
   - Disabled by default
   - Enable with `--allow-network` flag
   - Configure in `config.at` file

## Quick Start

Create your first AT++ program:

```at
// hello.at
func main() {
    println("Hello from AT++!")
}
```

Run it:
```cmd
at++ hello.at
```

## Development Tools

1. **AT++ Shell**
   ```cmd
   at++
   ```
   Interactive development environment

2. **Compiler Options**
   ```cmd
   at++ compile --optimize hello.at
   ```
   Produces optimized bytecode

3. **Security Settings**
   ```cmd
   at++ --security=strict hello.at
   ```
   Run with enhanced security

4. **Debug Mode**
   ```cmd
   at++ --debug hello.at
   ```
   Enables detailed logging

For more information, visit the official documentation or run:
```cmd
at++ --help
```