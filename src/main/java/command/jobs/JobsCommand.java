package command.jobs;

import command.Command;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JobsCommand implements Command {

    private final List<Job> jobs;

    public JobsCommand(List<Job> jobs) {
        this.jobs = jobs;
    }

    @Override
    public boolean matches(String input, String[] inputArray) {
        return inputArray[0].equals("jobs");
    }

    @Override
    public Path execute(String input, String[] inputArray, Path currentDir) throws Exception {
        List<Job> finished = new ArrayList<>();
        int size = jobs.size();

        for (int i = 0; i < size; i++) {
            Job job = jobs.get(i);
            String marker = (i == size - 1) ? "+" : (i == size - 2) ? "-" : " ";

            if (job.process().isAlive()) {
                System.out.println(format(job.number(), marker, "Running", job.command() + " &"));
            } else {
                System.out.println(format(job.number(), marker, "Done", job.command()));
                finished.add(job);
            }
        }
        jobs.removeAll(finished);
        return currentDir;
    }

    private String format(int number, String marker, String status, String command) {
        return String.format("[%d]%s  %-24s%s", number, marker, status, command);
    }
}
