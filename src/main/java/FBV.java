import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FBV implements Scheduler {
    private List<Process> finishedProcesses = new ArrayList<>();
    private List<String> executionOrder = new ArrayList<>();
    private int[] timeQuanta = {2, 4, 4}; // Time quanta for the three levels

    @Override
    public void schedule(List<Process> processes, int dispatcherTime) {
        Queue<Process>[] queues = new LinkedList[3];
        for (int i = 0; i < 3; i++) {
            queues[i] = new LinkedList<>();
        }

        int currentTime = 0;
        Process currentProcess = null;
        int currentQueueLevel = 0;
        int timeInLowestQueue = 0; // Track time spent in the lowest priority queue
        int timeInCurrentSlice = 0;

        while (!processes.isEmpty() || !queues[0].isEmpty() || !queues[1].isEmpty() || !queues[2].isEmpty() || currentProcess != null) {
            // Move processes that have arrived to the highest priority queue
            while (!processes.isEmpty() && processes.get(0).getArrivalTime() <= currentTime) {
                queues[0].offer(processes.remove(0));
            }

            // If the current process is null or needs to switch, handle it
            if (currentProcess == null || currentProcess.getRemainingTime() <= 0 ||
                    (currentQueueLevel == 0 && timeInCurrentSlice >= timeQuanta[0]) ||
                    (currentQueueLevel == 1 && timeInCurrentSlice >= timeQuanta[1]) ||
                    (currentQueueLevel == 2 && timeInCurrentSlice >= timeQuanta[2])) {

                if (currentProcess != null) {
                    if (currentProcess.getRemainingTime() > 0) {
                        if (currentQueueLevel < 2) {
                            queues[currentQueueLevel + 1].offer(currentProcess); // Move to a lower priority queue
                        } else {
                            timeInLowestQueue += timeInCurrentSlice;
                            if (timeInLowestQueue >= 16) {
                                queues[0].offer(currentProcess); // Move back to the highest priority queue
                                timeInLowestQueue = 0;
                            } else {
                                queues[currentQueueLevel].offer(currentProcess); // Stay in the same queue (Round-robin)
                            }
                        }
                    } else {
                        currentProcess.setFinishTime(currentTime);
                        finishedProcesses.add(currentProcess);
                    }
                    currentProcess = null;
                    timeInCurrentSlice = 0; // Reset the time slice counter
                }

                // Select the next process to run from the highest priority available queue
                for (int i = 0; i < 3; i++) {
                    if (!queues[i].isEmpty()) {
                        currentProcess = queues[i].poll();
                        currentQueueLevel = i;
                        currentTime += dispatcherTime; // Always add dispatcher time before a new process starts
                        executionOrder.add("T" + currentTime + ": " + currentProcess.getId());
                        break;
                    }
                }
            }

            // Run the current process
            if (currentProcess != null) {
                int timeSlice = timeQuanta[currentQueueLevel];
                int actualTime = Math.min(timeSlice - timeInCurrentSlice, currentProcess.getRemainingTime());
                currentProcess.runFor(actualTime);
                currentTime += actualTime;
                timeInCurrentSlice += actualTime;

                // Check if the current process has finished
                if (currentProcess.isFinished()) {
                    currentProcess.setFinishTime(currentTime);
                    finishedProcesses.add(currentProcess);
                    currentProcess = null;
                    timeInLowestQueue = 0; // Reset the counter for time spent in the lowest priority queue
                    timeInCurrentSlice = 0;
                }
            } else {
                currentTime++;
            }
        }

        printResults();
    }

    @Override
    public void printResults() {
        System.out.println();
        System.out.println("FBV:");

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
