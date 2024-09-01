import java.util.ArrayList;
import java.util.List;

public class SRT implements Scheduler {
    private List<Process> finishedProcesses = new ArrayList<>();
    private List<String> executionOrder = new ArrayList<>();

    @Override
    public void schedule(List<Process> processes, int dispatcherTime) {
        List<Process> readyQueue = new ArrayList<>();
        int currentTime = 0;
        Process currentProcess = null;

        while (!processes.isEmpty() || !readyQueue.isEmpty() || currentProcess != null) {
            // Move processes that have arrived to the ready queue
            while (!processes.isEmpty() && processes.get(0).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.remove(0));
            }

            // Sort the ready queue by the remaining time (Shortest Remaining Time first)
            readyQueue.sort((p1, p2) -> Integer.compare(p1.getRemainingTime(), p2.getRemainingTime()));

            // Handle context switching if necessary
            if (currentProcess == null && !readyQueue.isEmpty()) {
                // Select the first process from the queue
                currentProcess = readyQueue.remove(0);
                currentTime += dispatcherTime;  // Add dispatcher time when a new process is selected
                executionOrder.add("T" + currentTime + ": " + currentProcess.getId());
                if (currentProcess.getStartTime() == 0) {
                    currentProcess.setStartTime(currentTime);
                }
            } else if (currentProcess != null && !readyQueue.isEmpty() && currentProcess.getRemainingTime() > readyQueue.get(0).getRemainingTime()) {
                // If a process with a shorter remaining time arrives, preempt the current process
                readyQueue.add(currentProcess);
                currentProcess = readyQueue.remove(0);
                currentTime += dispatcherTime;  // Add dispatcher time for context switch
                executionOrder.add("T" + currentTime + ": " + currentProcess.getId());
                if (currentProcess.getStartTime() == 0) {
                    currentProcess.setStartTime(currentTime);
                }
            }

            // Run the process for 1ms
            if (currentProcess != null) {
                currentProcess.runFor(1);
                currentTime++;

                // Check if the current process is finished
                if (currentProcess.isFinished()) {
                    currentProcess.setFinishTime(currentTime);
                    finishedProcesses.add(currentProcess);
                    currentProcess = null;
                }
            } else {
                // No process is running; just move time forward
                currentTime++;
            }
        }

        printResults();
    }

    @Override
    public void printResults() {
        System.out.println();
        System.out.println("SRT:");

        // Print the execution order
        for (String log : executionOrder) {
            System.out.println(log);
        }

        // Sort finished processes by their ID (to ensure correct order in the output)
        finishedProcesses.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));

        System.out.println("\nProcess  Turnaround Time  Waiting Time");
        for (Process p : finishedProcesses) {
            System.out.printf("%s       %-17d %d\n", p.getId(), p.getTurnaroundTime(), p.getWaitingTime());
        }
    }

    public double getAverageTurnaroundTime() {
        return finishedProcesses.stream().mapToDouble(Process::getTurnaroundTime).average().orElse(0.0);
    }

    public double getAverageWaitingTime() {
        return finishedProcesses.stream().mapToDouble(Process::getWaitingTime).average().orElse(0.0);
    }
}
