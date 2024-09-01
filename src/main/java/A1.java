import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class A1 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java A1 <input file>");
            return;
        }

        String inputFileName = args[0];
        try (Scanner scanner = new Scanner(new File(inputFileName))) {
            int dispatcherTime = 0;
            List<Process> processes = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("DISP:")) {
                    dispatcherTime = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("PID:")) {
                    String id = line.split(":")[1].trim();
                    int arrivalTime = Integer.parseInt(scanner.nextLine().split(":")[1].trim());
                    int serviceTime = Integer.parseInt(scanner.nextLine().split(":")[1].trim());
                    int tickets = Integer.parseInt(scanner.nextLine().split(":")[1].trim());
                    processes.add(new Process(id, arrivalTime, serviceTime, tickets));
                }
            }

            // Schedule using FCFS
            FCFS fcfs = new FCFS();
            fcfs.schedule(processes, dispatcherTime);

            // Schedule using SRT
            SRT srt = new SRT();
            srt.schedule(processes, dispatcherTime);

            // Schedule using FBV
/*            FBV fbv = new FBV();
            fbv.schedule(processes, dispatcherTime);*/

            // Print Summary
            System.out.println("\nSummary");
            System.out.printf("Algorithm  Average Turnaround Time  Waiting Time\n");
            System.out.printf("FCFS       %-23.2f %-14.2f\n", fcfs.getAverageTurnaroundTime(), fcfs.getAverageWaitingTime());
            System.out.printf("SRT        %-23.2f %-14.2f\n", srt.getAverageTurnaroundTime(), srt.getAverageWaitingTime());
            //System.out.printf("FBV        %-23.2f %-14.2f\n", fbv.getAverageTurnaroundTime(), fbv.getAverageWaitingTime());
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + inputFileName);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
