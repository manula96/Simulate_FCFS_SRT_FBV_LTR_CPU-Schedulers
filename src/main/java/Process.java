class Process {
    String pid;
    int arrivalTime;
    int serviceTime;
    int tickets;
    int remainingTime;
    int turnaroundTime;
    int waitingTime;

    Process(String pid, int arrivalTime, int serviceTime, int tickets) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.tickets = tickets;
        this.remainingTime = serviceTime;
        this.turnaroundTime = 0;
        this.waitingTime = 0;
    }

    @Override
    public String toString() {
        return String.format("Process(pid=%s, arrivalTime=%d, serviceTime=%d, tickets=%d, remainingTime=%d, turnaroundTime=%d, waitingTime=%d)",
                pid, arrivalTime, serviceTime, tickets, remainingTime, turnaroundTime, waitingTime);
    }
}