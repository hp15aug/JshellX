import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
         System.out.print("$ ");

         Scanner sc=new Scanner(System.in);

         while(true){
             String input = sc.nextLine();
             String[] inputArray = input.split(" ");
             StringBuilder sb=new StringBuilder();
             if(input.equalsIgnoreCase("exit") && inputArray.length == 1)
                 break;
             else if(inputArray[0].equalsIgnoreCase("echo")){
                 for (int i = 1; i < inputArray.length; i++) {
                     sb.append(inputArray[i]+" ");
                 }
                 System.out.println(sb.toString());
             }else{
                 System.out.println(input + ": command not found");
             }
             System.out.print("$ ");
         }

    }
}
