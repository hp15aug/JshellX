import command.Command;
import command.cat.CatCommand;
import command.cd.CdCommand;
import command.echo.EchoCommand;
import command.exit.ExitCommand;
import command.pwd.PwdCommand;
import command.type.Type;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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

            for (Command command : commandHandlers) {
                if (command.matches(input, inputArray)) {
                    currentDirectory = command.execute(input, inputArray, currentDirectory);
                    commandHandled = true;
                    break;
                }
            }

            if (!commandHandled) {
                executeExternalCommand(input, inputArray, currentDirectory);
            }
        }
    }

    private static void executeExternalCommand(String input, String[] inputArray, Path currentDirectory)
            throws Exception {

        String commandName = inputArray[0];
        String executablePath = Command.isAvailable(commandName);

        if (!executablePath.isEmpty()) {
            ProcessBuilder processBuilder = new ProcessBuilder(inputArray);
            processBuilder.directory(currentDirectory.toFile());
            processBuilder.inheritIO().start().waitFor();
        } else {
            System.out.println(input + ": command not found");
        }
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
