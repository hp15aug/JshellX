package command.echo;

import command.Command;

import java.nio.file.Path;

public class EchoCommand implements Command {
    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("echo");
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        String res = input.length() > 5 ? input.substring(5) : "";

        char[] resArray = res.toCharArray();

        StringBuilder sb=new StringBuilder();
        if(resArray.length > 0 && resArray[0] == '\'') {
            for (int i = 1; i < resArray.length; i++) {
                if(resArray[i] == '\'') continue;
                sb.append(resArray[i]);
            }
            System.out.println(sb);
        } else{
            String[] temp = res.split(" +");
            for(String s:temp){
                sb.append(s).append(" ");
            }
            System.out.println(sb.toString().trim());
        }
        return currentDir;
    }
}
