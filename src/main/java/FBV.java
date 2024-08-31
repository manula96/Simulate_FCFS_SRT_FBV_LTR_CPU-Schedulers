import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FBV implements Scheduler {
    private List<Process> processes;
    private int dispatcherTime;
    private double averageTurnaroundTime;
    private double averageWaitingTime;

    @Override
    public void schedule(List<Process> processes, int dispatcherTime) {
        this.processes = processes;
        this.dispatcherTime = dispatcherTime;

        Queue<Process> queue1 = new LinkedList<>();
        Queue<Process> queue2 = new LinkedList<>();
        Queue<Process> queue3 = new LinkedList<>();

        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;

        // Add processes to the first queue based on their arrival time
        queue1.addAll(processes);

        System.out.println("\nFBV:");

        while (!queue1.isEmpty() || !queue2.isEmpty() || !queue3.isEmpty()) {
            Process process = null;

            if (!queue1.isEmpty()) {
                process = queue1.poll();
                currentTime = Math.max(currentTime, process.getArrivalTime()) + dispatcherTime;
                currentTime = executeProcess(process, currentTime, 2, queue2);
            } else if (!queue2.isEmpty()) {
                process = queue2.poll();
                currentTime += dispatcherTime;
                currentTime = executeProcess(process, currentTime, 4, queue3);
            } else if (!queue3.isEmpty()) {
                process = queue3.poll();
                currentTime += dispatcherTime;
                if (currentTime - process.getStartTime() > 16) {
                    // If the process has been in the lowest priority queue for more than 16ms
                    queue1.add(process);
                } else {
                    currentTime = executeProcess(process, currentTime, 4, queue3);
                }
            }

            if (process != null && process.isFinished()) {
                totalTurnaroundTime += process.getTurnaroundTime();
                totalWaitingTime += process.getWaitingTime();
                System.out.println("T" + process.getStartTime() + ": " + process.getId());
            }
        }

        averageTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        averageWaitingTime = (double) totalWaitingTime / processes.size();

        printResults();
    }

    private int executeProcess(Process process, int currentTime, int timeQuantum, Queue<Process> nextQueue) {
        process.setStartTime(currentTime);

        if (process.getRemainingTime() > timeQuantum) {
            process.runFor(timeQuantum);
            currentTime += timeQuantum;
            nextQueue.add(process);
        } else {
            currentTime += process.getRemainingTime();
            process.runFor(process.getRemainingTime());
            process.setFinishTime(currentTime);
        }

        return currentTime;
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
