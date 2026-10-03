package command.cat;

import command.Command;

import java.nio.file.Files;
import java.nio.file.Path;

public class CatCommand implements Command {
    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("cat");
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        for (int i = 1; i < inputArray.length; i++) {
            String fileName = inputArray[i];
            Path file = currentDir.resolve(fileName);

            if (!Files.isRegularFile(file)) {
                System.err.println("cat: " + fileName + ": No such file or directory");
                continue;
            }

            Files.copy(file, System.out);
            System.out.flush();
        }
        return currentDir ;
    }
}
