package command;

import java.io.File;
import java.nio.file.Path;

public interface Command {
    boolean matches(String input, String[] inputArray);
    Path execute(String input, String[] inputArray, Path currentDir) throws Exception;

    static String isAvailable(String str) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null)
            return "";

        for (String dir : pathEnv.split(File.pathSeparator)) {
            File file = new File(dir, str);
            if (file.exists() && file.isFile() && file.canExecute())
                return file.getAbsolutePath();
        }
        return "";
    }
}
