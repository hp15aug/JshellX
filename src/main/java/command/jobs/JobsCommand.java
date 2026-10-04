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
        reap(jobs, true);
        return currentDir;
    }

    public static void reap(List<Job> jobs, boolean showRunning) {
        List<Job> finished = new ArrayList<>();
        int size = jobs.size();

        for (int i = 0; i < size; i++) {
            Job job = jobs.get(i);
            String marker = (i == size - 1) ? "+" : (i == size - 2) ? "-" : " ";

            if (!job.process().isAlive()) {
                System.out.println(format(job.number(), marker, "Done", job.command()));
                finished.add(job);
            } else if (showRunning) {
                System.out.println(format(job.number(), marker, "Running", job.command() + " &"));
            }
        }
        jobs.removeAll(finished);
    }
    private static String format(int number, String marker, String status, String command) {
        return String.format("[%d]%s  %-24s%s", number, marker, status, command);
    }
}
