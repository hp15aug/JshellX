package command.jobs;

import command.Command;

import java.nio.file.Path;

public class JobsCommand implements Command {


    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("jobs");
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        return currentDir;
    }
}
