package jbpm.client.wrapper;

public enum TaskStatus {
    Created("Created"),
    Ready("Ready"),
    Reserved("Reserved"),
    InProgress("InProgress"),
    Completed("Completed"),
    Failed("Failed");

    private final String status;


    TaskStatus(final String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return status;
    }
}
