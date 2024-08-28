import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class A1 {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide an input file.");
            return;
        }

        String filename = args[0];
        List<Process> processList = new ArrayList<>();
        int dispatcherTime = 0;
        List<Integer> randomNumbers = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filename))) {
            String section = "";
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("BEGIN")) {
                    section = "PROCESS";
                } else if (line.equals("BEGINRANDOM")) {
                    section = "RANDOM";
                } else if (line.equals("END")) {
                    section = "";
                } else if (line.equals("ENDRANDOM")) {
                    section = "";
                } else if (section.equals("PROCESS")) {
                    if (line.startsWith("PID:")) {
                        String pid = line.substring(4).trim();
                        int arrivalTime = Integer.parseInt(scanner.nextLine().trim().substring(8).trim());
                        int serviceTime = Integer.parseInt(scanner.nextLine().trim().substring(8).trim());
                        int tickets = Integer.parseInt(scanner.nextLine().trim().substring(8).trim());
                        processList.add(new Process(pid, arrivalTime, serviceTime, tickets));
                    }
                } else if (section.equals("RANDOM")) {
                    if (line.matches("\\d+")) {
                        randomNumbers.add(Integer.parseInt(line));
                    }
                } else if (line.startsWith("DISP:")) {
                    dispatcherTime = Integer.parseInt(line.substring(5).trim());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
            return;
        }

        // Debug statements
        System.out.println("Process list: " + processList);
        System.out.println("Dispatcher Time: " + dispatcherTime);
        System.out.println("Random Numbers: " + randomNumbers);

        // Example of creating schedulers and running them
        Scheduler fcfs = new FCFS(new ArrayList<>(processList), dispatcherTime);
        fcfs.schedule();

        Scheduler srt = new SRT(new ArrayList<>(processList), dispatcherTime);
        srt.schedule();

        Scheduler fbv = new FBV(new ArrayList<>(processList), dispatcherTime);
        fbv.schedule();

        Scheduler ltr = new LTR(new ArrayList<>(processList), dispatcherTime, randomNumbers);
        ltr.schedule();
    }
}
