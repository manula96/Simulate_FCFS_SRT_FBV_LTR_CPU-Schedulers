import java.util.List;

public class FCFS implements Scheduler {
    private List<Process> processes;
    private int dispatcherTime;

    private double averageTurnaroundTime;
    private double averageWaitingTime;

    @Override
    public void schedule(List<Process> processes, int dispatcherTime) {
        this.processes = processes;
        this.dispatcherTime = dispatcherTime;

        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;

        System.out.println("FCFS:");

        for (Process process : processes) {
            if (currentTime < process.getArrivalTime()) {
                currentTime = process.getArrivalTime();
            }
            currentTime += dispatcherTime;
            process.setStartTime(currentTime);
            currentTime += process.getServiceTime();
            process.setFinishTime(currentTime);

            totalTurnaroundTime += process.getTurnaroundTime();
            totalWaitingTime += process.getWaitingTime();

            System.out.println("T" + process.getStartTime() + ": " + process.getId());
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
