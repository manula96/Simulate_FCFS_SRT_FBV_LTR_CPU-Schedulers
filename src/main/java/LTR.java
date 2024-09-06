import java.util.*;

public class LTR implements Scheduler {
    private List<Process> finishedProcesses = new ArrayList<>();
    private List<String> executionOrder = new ArrayList<>();
    private Queue<Integer> randomNumbers;
    private int timeQuantum = 3; // Fixed time quantum for LTR

    public LTR(Queue<Integer> randomNumbers) {
        this.randomNumbers = randomNumbers;
    }

    @Override
    public void schedule(List<Process> processes, int dispatcherTime) {
        Queue<Process> readyQueue = new LinkedList<>();
        List<Process> allProcesses = new ArrayList<>(processes);  // Track all processes
        int currentTime = 0;

        // Initial dispatcher time before starting the first process
        currentTime += dispatcherTime;

        while (!readyQueue.isEmpty() || !allProcesses.isEmpty()) {
            // Move processes that have arrived to the ready queue
            Iterator<Process> processIterator = allProcesses.iterator();
            while (processIterator.hasNext()) {
                Process p = processIterator.next();
                if (p.getArrivalTime() <= currentTime) {
                    readyQueue.offer(p);
                    processIterator.remove(); // Remove the process from the list once it's added to the readyQueue
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;  // No processes are ready to run, so move time forward
                continue;
            }

            // Total tickets calculation
            int totalTickets = readyQueue.stream().mapToInt(Process::getTickets).sum();

            // Use the next random number from the input file
            if (randomNumbers.isEmpty()) {
                throw new IllegalStateException("No more random numbers available to continue scheduling.");
            }
            int randomNum = randomNumbers.poll();
            int winnerTicket = randomNum % totalTickets;  // Scale down the random number to be within the range

            // Determine the winning process using a counter
            int counter = 0;
            Process currentProcess = null;
            for (Process p : readyQueue) {
                counter += p.getTickets();
                if (counter > winnerTicket) {
                    currentProcess = p; // Found the winner
                    break;
                }
            }

            if (currentProcess != null) {
                readyQueue.remove(currentProcess);

                // Process runs for the time quantum or until it finishes
                executionOrder.add("T" + currentTime + ": " + currentProcess.getId());

                int timeToRun = Math.min(timeQuantum, currentProcess.getRemainingTime());
                currentProcess.runFor(timeToRun);
                currentTime += timeToRun;

                // If the process finishes, log its completion and adjust totalTickets
                if (currentProcess.isFinished()) {
                    currentProcess.setFinishTime(currentTime);
                    finishedProcesses.add(currentProcess);
                } else {
                    // Process hasn't finished, so requeue it
                    readyQueue.offer(currentProcess);
                }

                // Add dispatcher time after every process switch
                currentTime += dispatcherTime;
            }
        }

        printResults();
    }

    @Override
    public void printResults() {
        System.out.println();
        System.out.println("LTR:");

        // Print the execution order
        for (String log : executionOrder) {
            System.out.println(log);
        }

        // Sort finished processes by their ID (to ensure correct order in the output)
        finishedProcesses.sort(Comparator.comparing(Process::getId));

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
