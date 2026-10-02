import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import java.util.Set;
public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
         System.out.print("$ ");

         Scanner sc=new Scanner(System.in);

        Path currentDir = Paths.get("").toAbsolutePath();

         while(true){
             String input = sc.nextLine();
             String[] inputArray = input.split(" ");

             Set<String> inBuiltCommands = new HashSet<>(Arrays.asList("echo", "exit", "type", "pwd", "cd"));

             String firstCmd = inputArray[0];

             //exit
             if(input.equalsIgnoreCase("exit") && inputArray.length == 1) {
                 break;
             }
             //echo
             else if(input.startsWith("echo ")){
                 System.out.println(input.substring(5));
             }
             //type
             else if(firstCmd.equalsIgnoreCase("type") && inputArray.length == 2){
                 String path= isAvailable(inputArray[1]);
                 if(inBuiltCommands.contains(inputArray[1]))
                    System.out.println(inputArray[1]+ " is a shell builtin");
                 else if (!path.isEmpty()) {
                     System.out.println(inputArray[1]+ " is "+ path);
                 } else
                     System.out.println(inputArray[1]+": not found");
             }
             //default
             else if(firstCmd.equalsIgnoreCase("pwd")){
                 System.out.println(currentDir);
             }

             else if(firstCmd.equalsIgnoreCase("cd")){
                 String target = inputArray.length > 1 ? inputArray[1] : "~";
                 String expanded = target;

                 if(target.equals("~") || target.startsWith("~/")){
                     expanded = System.getenv("HOME") + target.substring(1);
                 }

                 Path newPath = currentDir.resolve(expanded).normalize();

                 if(Files.isDirectory(newPath)){
                     currentDir = newPath;
                 } else {
                     System.out.println("cd: " + target + ": No such file or directory");
                 }
             }
             //default
             else {
                 String execFileName = isAvailable(firstCmd);
                 if(!execFileName.isEmpty()){
                     ProcessBuilder pb = new ProcessBuilder(inputArray);
                     pb.directory(currentDir.toFile());
                     pb.inheritIO().start().waitFor();
                 } else {
                     System.out.println(input + ": command not found");
                 }
             }
             System.out.print("$ ");
         }
    }
    private static String isAvailable(String str){
        String pathEnv = System.getenv("PATH");
        if(pathEnv == null)
            return "";

        for(String dir: pathEnv.split(File.pathSeparator)){
            File file = new File(dir, str);
            if(file.exists() && file.isFile() && file.canExecute())
                return file.getAbsolutePath();
        }
        return "";
    }
}
