package command.cd;

import command.Command;

import java.nio.file.Files;
import java.nio.file.Path;

public class CdCommand implements Command {
    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("cd");
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        String target = inputArray.length > 1 ? inputArray[1] : "~";
        String expanded = target;

        if (target.equals("~") || target.startsWith("~/")) {
            expanded = System.getenv("HOME") + target.substring(1);
        }

        Path newPath = currentDir.resolve(expanded).normalize();
        if (Files.isDirectory(newPath)) {
            return newPath;
        }

        System.out.println("cd: " + target + ": No such file or directory");
        return currentDir;
    }
}
