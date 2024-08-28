import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.*;

abstract class Scheduler {
    List<Process> processList;
    int dispatcherTime;

    Scheduler(List<Process> processList, int dispatcherTime) {
        this.processList = processList;
        this.dispatcherTime = dispatcherTime;
    }

    abstract void schedule();

    protected void printResults(String algorithm, List<String> output) {
        System.out.println(algorithm + ":");
        for (String line : output) {
            System.out.println(line);
        }

        System.out.println("\nProcess  Turnaround Time  Waiting Time");
        for (Process p : processList) {
            System.out.printf("%s       %d               %d%n", p.pid, p.turnaroundTime, p.waitingTime);
        }
        System.out.println();
    }
}

class FCFS extends Scheduler {
    FCFS(List<Process> processList, int dispatcherTime) {
        super(processList, dispatcherTime);
    }

    @Override
    void schedule() {
        List<String> output = new ArrayList<>();
        int currentTime = 0;

        for (Process process : processList) {
            process.waitingTime = currentTime - process.arrivalTime;
            currentTime += process.serviceTime;
            process.turnaroundTime = currentTime - process.arrivalTime;
            output.add("T" + (process.turnaroundTime) + ": " + process.pid);
        }

        printResults("FCFS", output);
    }
}

class SRT extends Scheduler {
    SRT(List<Process> processList, int dispatcherTime) {
        super(processList, dispatcherTime);
    }

    @Override
    void schedule() {
        List<String> output = new ArrayList<>();
        int currentTime = 0;
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remainingTime));

        // Convert processList to a mutable list to avoid ConcurrentModificationException
        List<Process> processQueue = new ArrayList<>(processList);

        while (!processQueue.isEmpty() || !queue.isEmpty()) {
            // Add processes that have arrived to the queue
            List<Process> toRemove = new ArrayList<>();
            for (Process p : processQueue) {
                if (p.arrivalTime <= currentTime) {
                    queue.add(p);
                    toRemove.add(p);
                }
            }
            processQueue.removeAll(toRemove);

            if (!queue.isEmpty()) {
                Process currentProcess = queue.poll();
                int serviceTime = currentProcess.remainingTime;

                // Update the output with the time and process ID
                output.add("T" + (currentTime + serviceTime) + ": " + currentProcess.pid);
                currentTime += serviceTime;
                currentProcess.remainingTime = 0;
                currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.serviceTime;
            } else {
                // If queue is empty, move time forward
                currentTime++;
            }
        }

        printResults("SRT", output);
    }
}

class FBV extends Scheduler {
    FBV(List<Process> processList, int dispatcherTime) {
        super(processList, dispatcherTime);
    }

    @Override
    void schedule() {
        List<String> output = new ArrayList<>();
        int currentTime = 0;
        int timeQuantum = 4;
        int index = 0;

        while (!processList.isEmpty()) {
            Process process = processList.get(index % processList.size());
            int executionTime = Math.min(timeQuantum, process.remainingTime);
            output.add("T" + (currentTime + executionTime) + ": " + process.pid);
            currentTime += executionTime;
            process.remainingTime -= executionTime;

            if (process.remainingTime == 0) {
                processList.remove(process);
            }
            index++;
        }

        printResults("FBV", output);
    }
}

class LTR extends Scheduler {
    List<Integer> randomNumbers;

    LTR(List<Process> processList, int dispatcherTime, List<Integer> randomNumbers) {
        super(processList, dispatcherTime);
        this.randomNumbers = randomNumbers;
    }

    @Override
    void schedule() {
        List<String> output = new ArrayList<>();
        int currentTime = 0;
        int index = 0;
        int timeQuantum = 4;

        while (!processList.isEmpty()) {
            int randomIndex = index % randomNumbers.size();
            Process process = processList.get(randomIndex % processList.size());
            int executionTime = Math.min(timeQuantum, process.remainingTime);
            output.add("T" + (currentTime + executionTime) + ": " + process.pid);
            currentTime += executionTime;
            process.remainingTime -= executionTime;

            if (process.remainingTime == 0) {
                processList.remove(process);
            }
            index++;
        }

        printResults("LTR", output);
    }
}
