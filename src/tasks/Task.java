package tasks;

public class Task {

    private String name;
    private String description;
    private Status status = Status.NEW;
    private Integer id;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(Integer id, String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.setStatus(status);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name = " + name + '\'' +
                ", description = " + description + '\'' +
                ", status = " + status +
                ", id = " + id +
                "}"+"\n";
    }
}
