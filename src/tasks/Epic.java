package tasks;

import java.util.ArrayList;
public class Epic extends Task {
    private final ArrayList<Integer> catalogSubtaskId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.setStatus(Status.NEW);
    }

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public void addSubtaskIdInEpic(Integer subtaskId){
        catalogSubtaskId.add(subtaskId);
    }

    public ArrayList<Integer> getCatalogSubtaskId() {
        return catalogSubtaskId;
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
        return "Epic{" +
                "name = " + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", status = " +  getStatus() +
                ", id = " + getId() +
                "}"+"\n";
    }
}