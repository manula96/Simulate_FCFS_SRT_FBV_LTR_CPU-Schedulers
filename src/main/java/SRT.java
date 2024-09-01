import java.util.List;
import java.util.ArrayList;

public class SRT implements Scheduler {
    private List<Process> completedProcesses = new ArrayList<>();
    private double averageTurnaroundTime = 0.0;
    private double averageWaitingTime = 0.0;

    @Override
    public void schedule(List<Process> processes, int dispatcherTime) {
        int currentTime = 0;
        List<Process> readyQueue = new ArrayList<>();
        Process currentProcess = null;

        while (!processes.isEmpty() || !readyQueue.isEmpty() || currentProcess != null) {
            // Add all processes that have arrived to the ready queue
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).getArrivalTime() <= currentTime) {
                    readyQueue.add(processes.remove(i));
                    i--; // Adjust for removal
                }
            }

            // Sort readyQueue by remaining time (SRT)
            readyQueue.sort((p1, p2) -> Integer.compare(p1.getRemainingTime(), p2.getRemainingTime()));

            if (currentProcess == null && !readyQueue.isEmpty()) {
                // Pick the next process from the ready queue
                currentProcess = readyQueue.remove(0);
                // Simulate the dispatcher delay
                currentTime += dispatcherTime;
                if (currentProcess.getStartTime() == 0) {
                    currentProcess.setStartTime(currentTime);
                }
            }

            if (currentProcess != null) {
                // Run the process for 1 unit of time
                currentProcess.runFor(1);
                currentTime++;

                // Check if this process finishes
                if (currentProcess.isFinished()) {
                    currentProcess.setFinishTime(currentTime);
                    completedProcesses.add(currentProcess);
                    currentProcess = null;  // Reset to select a new process
                } else {
                    // If a new process arrives with less remaining time, preempt
                    if (!readyQueue.isEmpty() && readyQueue.get(0).getRemainingTime() < currentProcess.getRemainingTime()) {
                        readyQueue.add(currentProcess);
                        readyQueue.sort((p1, p2) -> Integer.compare(p1.getRemainingTime(), p2.getRemainingTime()));
                        currentProcess = null;
                    }
                }
            } else {
                // If no process is ready, just move time forward
                currentTime++;
            }
        }

        calculateAverages();
        printResults();
    }

    private void calculateAverages() {
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        for (Process p : completedProcesses) {
            totalTurnaroundTime += p.getTurnaroundTime();
            totalWaitingTime += p.getWaitingTime();
        }
        averageTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();
        averageWaitingTime = (double) totalWaitingTime / completedProcesses.size();
    }

    @Override
    public void printResults() {
        System.out.println("\nSRT:");
        for (Process p : completedProcesses) {
            System.out.printf("T%d: %s\n", p.getFinishTime() - p.getServiceTime(), p.getId());
        }
        System.out.println("\nProcess  Turnaround Time  Waiting Time");
        for (Process p : completedProcesses) {
            System.out.printf("%-9s %-15d %-12d\n", p.getId(), p.getTurnaroundTime(), p.getWaitingTime());
        }
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }
}
