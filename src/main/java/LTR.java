import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

        while (!readyQueue.isEmpty() || !allProcesses.isEmpty()) {
            // Move processes that have arrived to the ready queue
            while (!allProcesses.isEmpty() && allProcesses.get(0).getArrivalTime() <= currentTime) {
                readyQueue.offer(allProcesses.remove(0));
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            // Total tickets calculation
            int totalTickets = 0;
            for (Process p : readyQueue) {
                totalTickets += p.getTickets();
            }

            // Draw a random number to determine the winner
            int randomNum = randomNumbers.poll();
            int winnerTicket = randomNum % totalTickets;

            // Diagnostic Output
            System.out.println("Time: " + currentTime + ", Random Number: " + randomNum + ", Winner Ticket: " + winnerTicket);

            // Determine the winning process
            int ticketCounter = 0;
            Process currentProcess = null;
            for (Process p : readyQueue) {
                ticketCounter += p.getTickets();
                if (ticketCounter > winnerTicket) {
                    currentProcess = p;
                    break;
                }
            }

            if (currentProcess != null) {
                readyQueue.remove(currentProcess);
                currentTime += dispatcherTime; // Apply dispatcher time before running the process
                executionOrder.add("T" + currentTime + ": " + currentProcess.getId());

                int timeToRun = Math.min(timeQuantum, currentProcess.getRemainingTime());
                currentProcess.runFor(timeToRun);
                currentTime += timeToRun;

                if (currentProcess.isFinished()) {
                    currentProcess.setFinishTime(currentTime);
                    finishedProcesses.add(currentProcess);
                } else {
                    readyQueue.offer(currentProcess); // Re-enter the process at the end of the queue
                }
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
