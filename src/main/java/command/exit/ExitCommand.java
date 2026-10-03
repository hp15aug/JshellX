package command.exit;

import command.Command;

import java.nio.file.Path;

public class ExitCommand implements Command {

    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("exit") && inputArray.length == 1;
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        System.exit(0);
        return currentDir;
    }
}
