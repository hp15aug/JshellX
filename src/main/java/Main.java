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
             else if(input.startsWith("echo ")){
                    System.out.println(input.substring(5));
             }else{
                 System.out.println(input + ": command not found");
             }
             System.out.print("$ ");
         }

    }
}
