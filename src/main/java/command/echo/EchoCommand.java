package command.echo;

import command.Command;

import java.nio.file.Path;

public class EchoCommand implements Command {
    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("echo");
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        System.out.println(input.substring(5));
        return currentDir;
    }
}
