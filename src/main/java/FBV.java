/*
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FBV implements Scheduler {
    private List<Process> processes;

    private double averageTurnaroundTime;
    private double averageWaitingTime;

    private static class ProcessWithQueue3Time {
        Process process;
        int timeInQueue3;

        ProcessWithQueue3Time(Process process) {
            this.process = process;
            this.timeInQueue3 = 0;
        }
    }

    @Override
    public void schedule(List<Process> processes, int dispatcherTime) {
        this.processes = new LinkedList<>(processes);

        Queue<Process> queue1 = new LinkedList<>();
        Queue<Process> queue2 = new LinkedList<>();
        Queue<ProcessWithQueue3Time> queue3 = new LinkedList<>();

        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;

        for (Process process : processes) {
            queue1.add(process);
        }

        System.out.println("\nFBV:");

        while (!queue1.isEmpty() || !queue2.isEmpty() || !queue3.isEmpty()) {
            Process process = null;

            if (!queue1.isEmpty()) {
                process = queue1.poll();
                currentTime = Math.max(currentTime, process.getArrivalTime());
                currentTime += dispatcherTime;
                //System.out.println("Q1");

                System.out.println("T" + currentTime + ": " + process.getId());
                currentTime = executeProcess(process, currentTime, 2, queue2);
            } else if (!queue2.isEmpty()) {
                process = queue2.poll();
                //System.out.println("Q2");

                currentTime += dispatcherTime;
                System.out.println("T" + currentTime + ": " + process.getId());
                currentTime = executeProcess(process, currentTime, 4, queue3);
            } else if (!queue3.isEmpty()) {
                ProcessWithQueue3Time pwqt = queue3.poll();
                process = pwqt.process;
                //System.out.println("Q3");

                currentTime += dispatcherTime;
                System.out.println("T" + currentTime + ": " + process.getId());
                currentTime = executeProcessInQueue3(pwqt, currentTime, 4, queue1, queue3);
            } else {
                currentTime++;
            }

            if (process != null && process.isFinished()) {
                process.setFinishTime(currentTime);
                totalTurnaroundTime += process.getTurnaroundTime();
                totalWaitingTime += process.getWaitingTime();
            }
        }

        averageTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        averageWaitingTime = (double) totalWaitingTime / processes.size();

        printResults();
    }

    private int executeProcess(Process process, int currentTime, int timeQuantum, Queue<?> nextQueue) {
        if (process.getStartTime() == 0) {
            process.setStartTime(currentTime);
        }

        int runTime = Math.min(process.getRemainingTime(), timeQuantum);
        process.runFor(runTime);
        currentTime += runTime;

        if (process.getRemainingTime() > 0) {
            if (nextQueue instanceof Queue) {
                ((Queue<Process>) nextQueue).add(process);
            } else if (nextQueue instanceof Queue) {
                ((Queue<ProcessWithQueue3Time>) nextQueue).add(new ProcessWithQueue3Time(process));
            }
        } else {
            process.setFinishTime(currentTime);
        }

        return currentTime;
    }

    private int executeProcessInQueue3(ProcessWithQueue3Time pwqt, int currentTime, int timeQuantum, Queue<Process> queue1, Queue<ProcessWithQueue3Time> queue3) {
        Process process = pwqt.process;

        int runTime = Math.min(process.getRemainingTime(), timeQuantum);
        process.runFor(runTime);
        currentTime += runTime;

        pwqt.timeInQueue3 += runTime;

        if (process.getRemainingTime() > 0) {
            if (pwqt.timeInQueue3 >= 16) {
                System.out.println("T" + currentTime + ": " + process.getId() + " (Boosted)");
                queue1.add(process);
            } else {
                queue3.add(pwqt);
            }
        } else {
            process.setFinishTime(currentTime);
        }

        return currentTime;
    }

    @Override
    public void printResults() {
        System.out.println("\nProcess  Turnaround Time  Waiting Time");
        for (Process process : processes) {
            int turnaroundTime = process.getFinishTime() - process.getArrivalTime();
            int waitingTime = turnaroundTime - process.getServiceTime();
            System.out.printf("%-9s %-17d %-13d\n", process.getId(), turnaroundTime, waitingTime);
        }

        System.out.println("\nSummary");
        System.out.printf("Algorithm  Average Turnaround Time  Average Waiting Time\n");
        System.out.printf("FBV        %.2f                    %.2f\n", averageTurnaroundTime, averageWaitingTime);
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }
}
*/
