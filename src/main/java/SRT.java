import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

public class SRT implements Scheduler {
    private List<Process> processes;
    private double averageTurnaroundTime;
    private double averageWaitingTime;

    @Override
    public void schedule(List<Process> processes, int dispatcherTime) {
        this.processes = processes;
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(Process::getRemainingTime)
                        .thenComparing(Process::getId)
        );

        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int completedProcesses = 0;
        Process currentProcess = null;

        System.out.println("\nSRT:");

        while (completedProcesses < processes.size()) {
            // Add newly arrived processes to the ready queue
            for (Process process : processes) {
                if (process.getArrivalTime() == currentTime && !process.isFinished()) {
                    readyQueue.add(process);
                }
            }

            // If no current process or need to switch due to shorter remaining time
            if (currentProcess == null || currentProcess.isFinished() ||
                    (!readyQueue.isEmpty() && readyQueue.peek().getRemainingTime() < currentProcess.getRemainingTime())) {

                if (currentProcess != null && !currentProcess.isFinished()) {
                    readyQueue.add(currentProcess);
                }

                if (!readyQueue.isEmpty()) {
                    currentProcess = readyQueue.poll();

                    // Dispatcher time rules: dispatcher only considers processes that arrived before or at the current time
                    currentTime += dispatcherTime; // Apply dispatcher time when switching processes
                    if (currentProcess.getStartTime() == 0) {
                        currentProcess.setStartTime(currentTime);
                    }

                    System.out.println("T" + currentTime + ": " + currentProcess.getId());
                }
            }

            // Execute the current process for one time unit
            if (currentProcess != null) {
                currentProcess.runFor(1);
                currentTime++;

                // If the current process finishes, calculate its times
                if (currentProcess.isFinished()) {
                    currentProcess.setFinishTime(currentTime);
                    totalTurnaroundTime += currentProcess.getTurnaroundTime();
                    totalWaitingTime += currentProcess.getWaitingTime();
                    completedProcesses++;
                }
            } else {
                currentTime++; // Increment time even if no process is ready
            }
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
