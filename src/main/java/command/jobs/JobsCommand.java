package command.jobs;

import command.Command;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JobsCommand implements Command {

    public record Job(int id, Process process, String command){}

    public static final List<Job> activeJobs = new ArrayList<>();

    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("jobs");
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        int size = activeJobs.size();
        for(int i=0; i<size; i++){
            Job job = activeJobs.get(i);
            if (size - 1 == i) {
                System.out.printf("[%d]+ %-24s%s\n", job.id(), "Running", job.command());
            }else if(size - 2 == i){
                System.out.printf("[%d]- %-24s%s\n", job.id(), "Running", job.command());
            }else{
                System.out.printf("[%d]  %-24s%s\n", job.id(), "Running", job.command());
            }
        }
        return currentDir;
    }
}
