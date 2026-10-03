import command.Command;
import command.cat.CatCommand;
import command.cd.CdCommand;
import command.echo.EchoCommand;
import command.exit.ExitCommand;
import command.pwd.PwdCommand;
import command.type.Type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final Set<String> builtInCommands = Set.of("echo", "exit", "type", "pwd", "cd");

    private record ParsedCommand(String[] tokens, Path stdoutFile, boolean appendStdout, Path stderrFile, boolean appendStderr) {}

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Path currentDirectory = Paths.get("").toAbsolutePath();

        List<Command> commandHandlers = List.of(
                new ExitCommand(),
                new EchoCommand(),
                new Type(builtInCommands),
                new PwdCommand(),
                new CatCommand(),
                new CdCommand());

        while (true) {
            System.out.print("$ ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                continue;
            }

            ParsedCommand parsed = extractRedirects(parseArguments(input), currentDirectory);
            if (parsed == null || parsed.tokens().length == 0) {
                continue;
            }
            String[] inputArray = parsed.tokens();

            Command handler = findHandler(commandHandlers, input, inputArray);
            if (handler != null) {
                currentDirectory = executeBuiltin(handler, input, inputArray, currentDirectory, parsed);
            } else {
                executeExternalCommand(input, inputArray, currentDirectory, parsed);
            }
        }
    }

    private static ParsedCommand extractRedirects(String[] tokens, Path currentDirectory) {
        List<String> commandTokens = new ArrayList<>();
        Path stdoutFile = null;
        Path stderrFile = null;
        boolean appendStdout = false;
        boolean appendStderr = false;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            boolean isStdoutOverwrite = token.equals(">") || token.equals("1>");
            boolean isStderrOverwrite = token.equals("2>");
            boolean isStdoutAppend = token.equals(">>") || token.equals("1>>");
            boolean isStderrAppend = token.equals("2>>");

            if (isStdoutOverwrite || isStderrOverwrite || isStderrAppend || isStdoutAppend) {
                if (i + 1 >= tokens.length) {
                    System.out.println("syntax error: expected file name after redirect");
                    return null;
                }
                Path file = currentDirectory.resolve(tokens[++i]);
                if (isStdoutOverwrite || isStdoutAppend) {
                    stdoutFile = file;
                    appendStdout = isStdoutAppend;
                } else if(isStderrOverwrite || isStderrAppend){
                    stderrFile = file;
                    appendStdout = isStderrAppend;
                }
            } else {
                commandTokens.add(token);
            }
        }
        return new ParsedCommand(commandTokens.toArray(new String[0]), stdoutFile, appendStdout, stderrFile, appendStderr);
    }

    private static Command findHandler(List<Command> handlers, String input, String[] inputArray) {
        for (Command command : handlers) {
            if (command.matches(input, inputArray)) {
                return command;
            }
        }
        return null;
    }

    private static Path executeBuiltin(Command handler, String input, String[] inputArray,
                                       Path currentDirectory, ParsedCommand parsed) throws Exception {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        PrintStream fileOut = null;
        PrintStream fileErr = null;

        try {
            if (parsed.stdoutFile() != null) {
                fileOut = new PrintStream(new FileOutputStream(parsed.stdoutFile().toFile(), parsed.appendStdout()));
                System.setOut(fileOut);
            }
            if (parsed.stderrFile() != null) {
                fileErr = new PrintStream(new FileOutputStream(parsed.stderrFile().toFile(), parsed.appendStderr()));
                System.setErr(fileErr);
            }
            return handler.execute(input, inputArray, currentDirectory);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
            if (fileOut != null) fileOut.close();
            if (fileErr != null) fileErr.close();
        }
    }

    private static void executeExternalCommand(String input, String[] inputArray,
                                               Path currentDirectory, ParsedCommand parsed) throws Exception {
        String commandName = inputArray[0];
        String executablePath = Command.isAvailable(commandName);

        if (executablePath.isEmpty()) {
            System.out.println(input + ": command not found");
            return;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(inputArray);
        processBuilder.directory(currentDirectory.toFile());
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

        if (parsed.stdoutFile() != null) {
            File outFile = parsed.stdoutFile().toFile();
            processBuilder.redirectOutput(parsed.appendStdout()
                    ? ProcessBuilder.Redirect.appendTo(outFile)
                    : ProcessBuilder.Redirect.to(outFile));
        } else {
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }

        if (parsed.stderrFile() != null) {
            File errFile = parsed.stderrFile().toFile();
            processBuilder.redirectError(parsed.appendStderr()
                    ? ProcessBuilder.Redirect.appendTo(errFile)
                    : ProcessBuilder.Redirect.to(errFile));
        } else {
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        }

        try {
            processBuilder.start().waitFor();
        } catch (IOException e) {
            System.out.println(commandName + ": " + e.getMessage());
        }
    }

    private static String[] parseArguments(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean tokenStarted = false;

        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == '\\' && inDoubleQuotes) {
                if (i + 1 < chars.length && (chars[i + 1] == '"' || chars[i + 1] == '\\')) {
                    current.append(chars[++i]);
                } else {
                    current.append(c);
                }
                tokenStarted = true;
            } else if (c == '\\' && !inSingleQuotes) {
                if (i + 1 < chars.length) {
                    current.append(chars[++i]);
                }
                tokenStarted = true;
            } else if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
                tokenStarted = true;
            } else if (c == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
                tokenStarted = true;
            } else if (c == ' ' && !inSingleQuotes && !inDoubleQuotes) {
                if (tokenStarted) {
                    tokens.add(current.toString());
                    current.setLength(0);
                    tokenStarted = false;
                }
            } else {
                current.append(c);
                tokenStarted = true;
            }
        }
        if (tokenStarted) {
            tokens.add(current.toString());
        }
        return tokens.toArray(new String[0]);
    }
}