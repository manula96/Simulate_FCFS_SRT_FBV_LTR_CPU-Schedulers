import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class A1_first {
    static class Process {
        String pid;
        int arrivalTime;
        int serviceTime;
        int tickets;
        int remainingTime;
        int waitingTime = 0;
        int turnaroundTime = 0;
        int startTime = -1;
        int endTime = -1;

        Process(String pid, int arrivalTime, int serviceTime, int tickets) {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.serviceTime = serviceTime;
            this.tickets = tickets;
            this.remainingTime = serviceTime;
        }
    }

    static int dispatcherTime = 0;
    static List<Process> processes = new ArrayList<>();
    static Queue<Integer> randomNumbers = new LinkedList<>();
    static List<Process> fcfsProcesses, srtProcesses, fbvProcesses, ltrProcesses;

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            System.out.println("Usage: java A1 input.txt");
            return;
        }

        parseInputFile(args[0]);

        fcfsProcesses = cloneProcessList(processes);
        simulateFCFS();

        srtProcesses = cloneProcessList(processes);
        simulateSRT();

        fbvProcesses = cloneProcessList(processes);
        simulateFBV();

        ltrProcesses = cloneProcessList(processes);
        simulateLTR();

        printSummary();
    }

    static void parseInputFile(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        boolean randomSection = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith("DISP:")) {
                dispatcherTime = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.startsWith("PID:")) {
                String pid = line.split(":")[1].trim();
                int arrivalTime = Integer.parseInt(scanner.nextLine().split(":")[1].trim());
                int serviceTime = Integer.parseInt(scanner.nextLine().split(":")[1].trim());
                int tickets = Integer.parseInt(scanner.nextLine().split(":")[1].trim());
                processes.add(new Process(pid, arrivalTime, serviceTime, tickets));
            } else if (line.equals("BEGINRANDOM")) {
                randomSection = true;
            } else if (line.equals("ENDRANDOM")) {
                randomSection = false;
            } else if (randomSection) {
                randomNumbers.add(Integer.parseInt(line));
            }
        }
        scanner.close();
    }

    static List<Process> cloneProcessList(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(new Process(p.pid, p.arrivalTime, p.serviceTime, p.tickets));
        }
        return copy;
    }

    static void simulateFCFS() {
        System.out.println("FCFS:");
        int currentTime = 1;  // Dispatcher starts at 1
        for (Process p : fcfsProcesses) {
            if (currentTime < p.arrivalTime) {
                currentTime = p.arrivalTime + dispatcherTime;
            }
            p.startTime = currentTime;
            currentTime += p.serviceTime;
            p.endTime = currentTime;
            p.turnaroundTime = p.endTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.serviceTime;

            System.out.printf("T%d: %s\n", p.startTime, p.pid);
        }
        printProcessTimes(fcfsProcesses);
    }

    static void simulateSRT() {
        System.out.println("\nSRT:");
        int currentTime = 1;  // Dispatcher starts at 1
        List<Process> readyQueue = new ArrayList<>();

        while (!srtProcesses.isEmpty() || !readyQueue.isEmpty()) {
            while (!srtProcesses.isEmpty() && srtProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue.add(srtProcesses.remove(0));
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            readyQueue.sort(Comparator.comparingInt(p -> p.remainingTime));
            Process currentProcess = readyQueue.get(0);

            if (currentProcess.startTime == -1) {
                currentProcess.startTime = currentTime;
            }

            currentProcess.remainingTime--;
            currentTime++;

            if (currentProcess.remainingTime == 0) {
                currentProcess.endTime = currentTime;
                currentProcess.turnaroundTime = currentProcess.endTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.serviceTime;
                System.out.printf("T%d: %s\n", currentProcess.startTime, currentProcess.pid);
                readyQueue.remove(currentProcess);
            }
        }
        printProcessTimes(srtProcesses);
    }

    static void simulateFBV() {
        System.out.println("\nFBV:");
        int currentTime = 1;  // Dispatcher starts at 1
        List<Process> readyQueue1 = new ArrayList<>();
        List<Process> readyQueue2 = new ArrayList<>();
        List<Process> readyQueue3 = new ArrayList<>();

        while (!fbvProcesses.isEmpty() || !readyQueue1.isEmpty() || !readyQueue2.isEmpty() || !readyQueue3.isEmpty()) {
            while (!fbvProcesses.isEmpty() && fbvProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue1.add(fbvProcesses.remove(0));
            }

            Process currentProcess = null;
            int timeQuantum = 0;

            if (!readyQueue1.isEmpty()) {
                currentProcess = readyQueue1.remove(0);
                timeQuantum = 2;
            } else if (!readyQueue2.isEmpty()) {
                currentProcess = readyQueue2.remove(0);
                timeQuantum = 4;
            } else if (!readyQueue3.isEmpty()) {
                currentProcess = readyQueue3.remove(0);
                timeQuantum = 4;
            } else {
                currentTime++;
                continue;
            }

            if (currentProcess.startTime == -1) {
                currentProcess.startTime = currentTime;
            }

            int timeSpent = Math.min(timeQuantum, currentProcess.remainingTime);
            currentProcess.remainingTime -= timeSpent;
            currentTime += timeSpent;

            if (currentProcess.remainingTime > 0) {
                if (timeQuantum == 2 && currentProcess.remainingTime > 16) {
                    readyQueue1.add(currentProcess);  // Move back to queue 1 if needed
                } else if (timeQuantum == 2) {
                    readyQueue2.add(currentProcess);
                } else {
                    readyQueue3.add(currentProcess);
                }
            } else {
                currentProcess.endTime = currentTime;
                currentProcess.turnaroundTime = currentProcess.endTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.serviceTime;
                System.out.printf("T%d: %s\n", currentProcess.startTime, currentProcess.pid);
            }
        }
        printProcessTimes(fbvProcesses);
    }

    static void simulateLTR() {
        System.out.println("\nLTR:");
        int currentTime = 1;  // Dispatcher starts at 1
        List<Process> readyQueue = new ArrayList<>();

        while (!ltrProcesses.isEmpty() || !readyQueue.isEmpty()) {
            while (!ltrProcesses.isEmpty() && ltrProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue.add(ltrProcesses.remove(0));
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            int totalTickets = readyQueue.stream().mapToInt(p -> p.tickets).sum();
            int winner = randomNumbers.poll() % totalTickets;
            int ticketSum = 0;
            Process currentProcess = null;

            for (Process p : readyQueue) {
                ticketSum += p.tickets;
                if (ticketSum > winner) {
                    currentProcess = p;
                    break;
                }
            }

            if (currentProcess == null) continue;

            if (currentProcess.startTime == -1) {
                currentProcess.startTime = currentTime;
            }

            int timeQuantum = 3;
            int timeSpent = Math.min(timeQuantum, currentProcess.remainingTime);
            currentProcess.remainingTime -= timeSpent;
            currentTime += timeSpent;

            if (currentProcess.remainingTime > 0) {
                readyQueue.add(currentProcess);
            } else {
                currentProcess.endTime = currentTime;
                currentProcess.turnaroundTime = currentProcess.endTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.serviceTime;
                System.out.printf("T%d: %s\n", currentProcess.startTime, currentProcess.pid);
                readyQueue.remove(currentProcess);
            }
        }
        printProcessTimes(ltrProcesses);
    }

    static void printProcessTimes(List<Process> processList) {
        System.out.println("\nProcess  Turnaround Time  Waiting Time");
        for (Process p : processList) {
            System.out.printf("%-8s %-16d %-12d\n", p.pid, p.turnaroundTime, p.waitingTime);
        }
        System.out.println();
    }

    static void printSummary() {
        System.out.println("Summary");
        System.out.println("Algorithm  Average Turnaround Time  Waiting Time");

        double fcfsTurnaroundAvg = fcfsProcesses.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double fcfsWaitingAvg = fcfsProcesses.stream().mapToInt(p -> p.waitingTime).average().orElse(0);

        double srtTurnaroundAvg = srtProcesses.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double srtWaitingAvg = srtProcesses.stream().mapToInt(p -> p.waitingTime).average().orElse(0);

        double fbvTurnaroundAvg = fbvProcesses.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double fbvWaitingAvg = fbvProcesses.stream().mapToInt(p -> p.waitingTime).average().orElse(0);

        double ltrTurnaroundAvg = ltrProcesses.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double ltrWaitingAvg = ltrProcesses.stream().mapToInt(p -> p.waitingTime).average().orElse(0);

        System.out.printf("FCFS       %.2f                    %.2f\n", fcfsTurnaroundAvg, fcfsWaitingAvg);
        System.out.printf("SRT        %.2f                    %.2f\n", srtTurnaroundAvg, srtWaitingAvg);
        System.out.printf("FBV        %.2f                    %.2f\n", fbvTurnaroundAvg, fbvWaitingAvg);
        System.out.printf("LTR        %.2f                    %.2f\n", ltrTurnaroundAvg, ltrWaitingAvg);
    }
}
