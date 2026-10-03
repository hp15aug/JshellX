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
        for(Job job: activeJobs){
            System.out.printf("[%d]+ %-24s%s\n", job.id(), "Running", job.command());
        }
        return currentDir;
    }
}
