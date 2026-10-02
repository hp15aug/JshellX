import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
         System.out.print("$ ");

         Scanner sc=new Scanner(System.in);

         while(true){
             String input = sc.nextLine();
             String[] inputArray = input.split(" ");
//             System.out.println(Arrays.toString(inputArray));

             Set<String> inBuiltCommands = new HashSet<>(Arrays.asList("echo", "exit", "type", "pwd"));

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
             else if(firstCmd.equalsIgnoreCase("pwd")){
                 System.out.println(System.getProperty("user.dir"));
             }
             //default
             else {
                 String execFileName = isAvailable(firstCmd);
                 if(!execFileName.isEmpty()){
                     ProcessBuilder pb = new ProcessBuilder(inputArray);
                     pb.inheritIO();
                     pb.start().waitFor();
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
