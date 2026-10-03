package command.type;

import command.Command;

import java.nio.file.Path;
import java.util.Set;

public class Type implements Command {
    private final Set<String> inBuiltCommands;

    public Type(Set<String> inBuiltCommands) {
        this.inBuiltCommands = inBuiltCommands;
    }

    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("type") && inputArray.length==2;
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        String path = Command.isAvailable(inputArray[1]);

        if (inBuiltCommands.contains(inputArray[1])) {
            System.out.println(inputArray[1] + " is a shell builtin");
        }else if (!path.isEmpty()) {
            System.out.println(inputArray[1] + " is " + path);
        }else {
            System.out.println(inputArray[1] + ": not found");
        }
        return currentDir;
    }
}
