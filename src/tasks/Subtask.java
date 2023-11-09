package tasks;

public class Subtask extends Task {

    protected int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
        this.setStatus(status);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public Status getStatus() {
        return super.getStatus();
    }

    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId = " + epicId +
                ", name = " + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", status = " +  getStatus() +
                ", id = " + getId() +
                '}'+ "\n";
    }
}