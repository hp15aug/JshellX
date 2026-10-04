# JshellX

JshellX is a lightweight, POSIX-compliant interactive command-line shell implemented in Java. It provides essential shell builtins, external process execution, robust quote parsing, standard I/O redirection, background job management, and interactive tab completion powered by JLine and a custom Trie data structure.

## Features

- **Built-in Commands**:
  - `cd`: Directory navigation with relative paths, absolute paths, and tilde (`~`) home directory expansion.
  - `pwd`: Displays the current absolute working directory.
  - `echo`: Text output supporting multi-word arguments and whitespace handling.
  - `type`: Inspects command types (identifies shell builtins or locates binaries on the system `$PATH`).
  - `jobs`: Lists active background tasks with POSIX-style markers (`+`, `-`) and reaps terminated processes.
  - `exit`: Terminates the shell session.

- **External Command Execution**:
  - Automatically resolves and executes system binaries found in `$PATH`.
  - Supports foreground process execution with inherited standard streams.
  - Supports background job execution using the `&` operator, returning the assigned job ID and process PID.

- **I/O Redirection**:
  - Standard output redirection (`>` and `1>`).
  - Standard error redirection (`2>`).
  - Append mode for stdout (`>>` and `1>>`) and stderr (`2>>`).
  - Seamless redirection support across both builtins and external commands.

- **Quote & Escape Parsing**:
  - Single quote parsing (`'...'`) preserving literal string contents.
  - Double quote parsing (`"..."`) with backslash escaping (`\"`, `\\`).
  - Backslash escaping for spaces and special characters outside quoted blocks.

- **Interactive Line Editing & Autocompletion**:
  - Built with JLine 3 for interactive terminal control and interrupt handling (`Ctrl+C`, `Ctrl+D`).
  - Prefix-based command autocompletion indexing builtins and executable binaries across `$PATH` using a Trie.
  - Single-tab inline completion for unambiguous matches, longest common prefix completion for partial matches, and double-tab display for multiple candidate options.

## Architecture & Project Structure

The project follows a modular Command pattern architecture, separating argument parsing, autocompletion indexing, and command execution into dedicated components.

```
src/main/java/
├── Main.java                 # Shell REPL loop, parsing, redirection, and dispatching
├── autocomplete/
│   └── Trie.java             # Prefix tree for fast command lookup and completion
└── command/
    ├── Command.java          # Command interface and PATH resolution utilities
    ├── cat/
    │   └── CatCommand.java   # File concatenation and printing
    ├── cd/
    │   └── CdCommand.java    # Directory navigation logic
    ├── echo/
    │   └── EchoCommand.java  # Echo implementation
    ├── exit/
    │   └── ExitCommand.java  # Exit handler
    ├── jobs/
    │   ├── Job.java          # Job record storing process and metadata
    │   └── JobsCommand.java  # Background job lifecycle management and formatting
    ├── pwd/
    │   └── PwdCommand.java   # Working directory reporting
    └── type/
        └── Type.java         # Builtin / binary type identification
```

## Prerequisites

- **Java Development Kit (JDK)**: Version 21+ (Java 26 preview features enabled in build configuration)
- **Apache Maven**: Version 3.8+

## Building and Running

### Build with Maven

Compile the project and package it into an executable JAR:

```bash
mvn clean package
```

This compiles the source files and creates a standalone JAR with all dependencies in the `target/` directory.

### Run the Shell

Execute the compiled JAR using Java:

```bash
java --enable-preview -jar target/codecrafters-shell.jar
```

Or run directly through Maven:

```bash
mvn exec:java -Dexec.mainClass="Main"
```

## Usage Examples

### Navigation and Builtins

```bash
$ pwd
/home/user/workspace

$ cd ..
$ pwd
/home/user

$ cd ~/Documents
$ pwd
/home/user/Documents

$ type cd
cd is a shell builtin

$ type ls
ls is /usr/bin/ls
```

### Argument Parsing and Quoting

```bash
$ echo "Hello    World"
Hello    World

$ echo 'literal $PATH and "quotes"'
literal $PATH and "quotes"

$ echo escaped\ space\ test
escaped space test
```

### Output and Error Redirection

```bash
$ echo "Log entry" > output.txt
$ echo "Append line" >> output.txt
$ ls non_existent_file 2> error.log
$ ls non_existent_file 2>> error.log
```

### Background Jobs

```bash
$ sleep 10 &
[1] 14205

$ jobs
[1]+  Running                 sleep 10 &

# After completion
$ jobs
[1]+  Done                    sleep 10
```

### Tab Autocompletion

- Typing `ec<TAB>` completes to `echo `.
- Typing a common prefix like `c<TAB>` completes the shared prefix, and pressing `<TAB>` again lists all matching commands found on `$PATH` and in builtins.

## License

This project is available under the MIT License.