package command.echo;

import command.Command;

import java.nio.file.Path;
import java.util.Arrays;

public class EchoCommand implements Command {
    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("echo");
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        String[] words = Arrays.copyOfRange(inputArray, 1, inputArray.length);
        System.out.println(String.join(" ", words));
        return currentDir;
    }
}