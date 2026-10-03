package command.pwd;

import command.Command;

import java.nio.file.Path;

public class PwdCommand implements Command {
    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("pwd");
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        System.out.println(currentDir);
        return currentDir;
    }
}
