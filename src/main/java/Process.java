public class Process {
    private String id;
    private int arrivalTime;
    private int serviceTime;
    private int remainingTime;
    private int tickets;

    private int startTime;
    private int finishTime;

    public Process(String id, int arrivalTime, int serviceTime, int tickets) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.remainingTime = serviceTime;  // Initially, remaining time is equal to service time
        this.tickets = tickets;
    }

    public String getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void runFor(int time) {
        remainingTime -= time;
    }

    public boolean isFinished() {
        return remainingTime <= 0;
    }

    public int getTickets() {
        return tickets;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getTurnaroundTime() {
        return finishTime - arrivalTime;
    }

    public int getWaitingTime() {
        return getTurnaroundTime() - serviceTime;
    }
}
