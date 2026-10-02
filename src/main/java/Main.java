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

             Set<String> inBuiltCommands = new HashSet<>();
             inBuiltCommands.add("echo");
             inBuiltCommands.add("exit");
             inBuiltCommands.add("type");

             String firstCmd = inputArray[0];

             StringBuilder sb=new StringBuilder();
             if(input.equalsIgnoreCase("exit") && inputArray.length == 1)
                 break;
             else if(input.startsWith("echo ")){
                 System.out.println(input.substring(5));
             }else if(firstCmd.equalsIgnoreCase("type") && inputArray.length == 2){
                 if(inBuiltCommands.contains(inputArray[1]))
                    System.out.println(inputArray[1]+ " is a shell builtin");
                 else
                     System.out.println(inputArray[1]+": not found");
             }
             else{
                 System.out.println(input + ": command not found");
             }
             System.out.print("$ ");
         }
    }
}
