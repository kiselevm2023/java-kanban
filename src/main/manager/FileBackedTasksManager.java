package main.manager;

import main.constants.Status;
import main.constants.TaskType;
import main.exceptions.ManagerSaveException;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final Path PATH = Path.of("tasks_file.csv");
    private File file = new File(String.valueOf(PATH));
    public static final String COMMA_SEPARATOR = ",";

    public FileBackedTasksManager(HistoryManager historyManager) {

        super(historyManager);
    }

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,name,status,description,epic" + "\n");
            for (Task task : getAllTasks()) {
                bufferedWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(toString(epic) + "\n");
            }
            for (SubTask subTask : getAllSubtasks()) {
                bufferedWriter.write(toString(subTask) + "\n");
            }
            bufferedWriter.write("\n");
            bufferedWriter.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл");
        }
    }

    private String getParentEpicId(Task task) {
        if (task instanceof SubTask) {
            return Integer.toString(((SubTask) task).getEpicId());
        }
        return "";
    }

    private TaskType getType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof SubTask) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    private String toString(Task task) {
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getName(),
                task.getStatus().toString(), task.getDescription(), String.valueOf(task.getStartTime()),
                String.valueOf(task.getDuration()), getParentEpicId(task)};
        return String.join(",", toJoin);
    }

    private Task fromString(String value) {
        String[] params = value.split(COMMA_SEPARATOR);
        int id = Integer.parseInt(params[0]);
        String type = params[1];
        String name = params[2];
        Status status = Status.valueOf(params[3].toUpperCase());
        String description = params[4];
        Instant startTime = Instant.parse(params[5]);
        long duration = Long.parseLong(params[6]);
        Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(params[7]) : null;

        if (type.equals("EPIC")) {
            Epic epic = new Epic(name, description, status, startTime, duration);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals("SUBTASK")) {
            SubTask subtask = new SubTask(name, description, status, epicId, startTime, duration);
            subtask.setId(id);
            return subtask;
        } else {
            Task task = new Task(name, description, status, startTime, duration);
            task.setId(id);
            return task;
        }
    }

    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder str = new StringBuilder();

        if (history.isEmpty()) {
            return "";
        }

        for (Task task : history) {
            str.append(task.getId()).append(",");
        }

        if (str.length() != 0) {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> list = new ArrayList<>();
        if (value != null) {
            String[] val = value.split(COMMA_SEPARATOR);
            for (String number : val) {
                list.add(Integer.parseInt(number));
            }
            return list;
        }
        return list;
    }

    public void loadFromFile() {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.equals("")) {
                    break;
                }

                Task task = fromString(line);

                if (task instanceof Epic) {
                    addEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    addSubTask((SubTask) task);
                } else {
                    addTask(task);
                }
            }

            String lineWithHistory = bufferedReader.readLine();
            for (int id : historyFromString(lineWithHistory)) {
                addToHistory(id);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public List<Epic> getAllEpics() {

        return super.getAllEpics();
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        super.updateStatusEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic epic) {
        super.deleteAllSubtasksByEpic(epic);
        save();
    }

    public static void main(String[] args) {

    }
}