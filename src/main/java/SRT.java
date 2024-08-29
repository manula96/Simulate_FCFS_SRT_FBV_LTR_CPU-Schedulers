import java.util.List;
import java.util.ArrayList;

public class SRT implements Scheduler {
    private List<Process> processes;
    private int dispatcherTime;

    private double averageTurnaroundTime;
    private double averageWaitingTime;

    @Override
    public void schedule(List<Process> processes, int dispatcherTime) {
        this.processes = processes;
        this.dispatcherTime = dispatcherTime;

        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        List<Process> completedProcesses = new ArrayList<>();

        System.out.println("\nSRT:");

        // A list to hold the current processes that are in the ready queue
        List<Process> readyQueue = new ArrayList<>();

        while (completedProcesses.size() < processes.size()) {
            // Add any arriving processes to the ready queue
            for (Process process : processes) {
                if (!readyQueue.contains(process) && !process.isFinished() && process.getArrivalTime() <= currentTime) {
                    readyQueue.add(process);
                }
            }

            // Find the process with the shortest remaining time in the ready queue
            Process currentProcess = null;
            for (Process process : readyQueue) {
                if (currentProcess == null || process.getRemainingTime() < currentProcess.getRemainingTime()) {
                    currentProcess = process;
                }
            }

            if (currentProcess == null) {
                currentTime++;
                continue;
            }

            // Simulate running the process for 1 unit of time
            System.out.println("T" + currentTime + ": " + currentProcess.getId());
            currentProcess.runFor(1);
            currentTime++;

            // Remove the process from the ready queue if it has finished execution
            if (currentProcess.isFinished()) {
                currentProcess.setFinishTime(currentTime);
                readyQueue.remove(currentProcess);
                completedProcesses.add(currentProcess);
            }
        }

        // Calculate turnaround time and waiting time for all processes
        for (Process process : processes) {
            totalTurnaroundTime += process.getTurnaroundTime();
            totalWaitingTime += process.getWaitingTime();
        }

        averageTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        averageWaitingTime = (double) totalWaitingTime / processes.size();

        printResults();
    }

    @Override
    public void printResults() {
        System.out.println("\nProcess  Turnaround Time  Waiting Time");
        for (Process process : processes) {
            System.out.printf("%-9s %-17d %-13d\n", process.getId(), process.getTurnaroundTime(), process.getWaitingTime());
        }
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }
}
