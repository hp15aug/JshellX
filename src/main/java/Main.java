import command.Command;
import command.cat.CatCommand;
import command.cd.CdCommand;
import command.echo.EchoCommand;
import command.exit.ExitCommand;
import command.pwd.PwdCommand;
import command.type.Type;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static final Set<String> builtInCommands = Set.of("echo", "exit", "type", "pwd", "cd");

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

            String[] inputArray = parseArguments(input);

            boolean commandHandled = false;
            Path redirectFile = null;
            int redirectIndex = findRedirectIndex(inputArray);

            if(redirectIndex != -1){
                if (redirectIndex + 1 >= inputArray.length) {
                    System.out.println("syntax error: expected file name after redirect");
                    continue;
                }
                redirectFile = currentDirectory.resolve(inputArray[redirectIndex +1]);
                inputArray = Arrays.copyOfRange(inputArray, 0, redirectIndex);
            }

            if(inputArray.length == 0)
                continue;

            Command handler = findHandler(commandHandlers, input, inputArray);
            if (handler != null) {
                currentDirectory = executeBuiltin(handler, input, inputArray, currentDirectory, redirectFile);
            }else{
                executeExternalCommand(input, inputArray, currentDirectory, redirectFile);
            }
        }
    }

    private static int findRedirectIndex(String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            if(tokens[i].equals(">") || tokens[i].equals("1>")){
                return i;
            }
        }
        return -1;
    }

    private static Command findHandler(List<Command> commandHandlers, String input, String[] inputArray) {
        for(Command command: commandHandlers){
            if (command.matches(input, inputArray)) {
                return command;
            }
        }
        return null;
    }

    private static Path executeBuiltin(Command handler, String input, String[] inputArray, Path currentDirectory, Path redirectFile) throws Exception {
        if (redirectFile == null) {
            return  handler.execute(input, inputArray, currentDirectory);
        }

        PrintStream original = System.out;
        try (PrintStream fileOut = new PrintStream(redirectFile.toFile())){
            System.setOut(fileOut);
            return handler.execute(input, inputArray, currentDirectory);
        }finally {
            System.setOut(original);
        }
    }

    private static void executeExternalCommand(String input, String[] inputArray, Path currentDirectory, Path redirectFile)
            throws Exception {

        String commandName = inputArray[0];
        String executablePath = Command.isAvailable(commandName);

        if (executablePath.isEmpty()) {
            System.out.println(input +": command not found");
            return;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(inputArray);
        processBuilder.directory(currentDirectory.toFile());

        if (redirectFile != null) {
            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectOutput(redirectFile.toFile());
        }else{
            processBuilder.inheritIO();
        }
        processBuilder.start().waitFor();
    }
    private static String[] parseArguments(String input){
        List<String> tokens = new ArrayList<>();
        StringBuilder curr = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean tokenStarted = false;

        char[] chars = input.toCharArray();

        for(int i=0; i<chars.length; i++){
            char ch = chars[i];

            if(ch == '\\' && inDoubleQuotes){
                if(i+1 < chars.length && (chars[i+1] == '"' || chars[i+1] == '\\')){
                    curr.append(chars[++i]);
                }else{
                    curr.append(ch);
                }
                tokenStarted = true;
            }else if(ch == '\\' && !inSingleQuotes){
                if(i+1 < chars.length){
                    curr.append(chars[++i]);
                }
                tokenStarted = true;
            } else if (ch == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
                tokenStarted = true;
            } else if(ch == '"' && !inSingleQuotes){
                inDoubleQuotes = !inDoubleQuotes;
                tokenStarted = true;
            }else if (ch == ' ' && !inSingleQuotes && !inDoubleQuotes){
                if (tokenStarted) {
                    tokens.add(curr.toString());
                    curr.setLength(0);
                    tokenStarted = false;
                }
            }else{
                curr.append(ch);
                tokenStarted = true;
            }
        }
        if (tokenStarted) {
            tokens.add(curr.toString());
        }
        return tokens.toArray(new String[0]);
    }
}
