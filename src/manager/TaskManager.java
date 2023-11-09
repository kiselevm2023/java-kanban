package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    Task addTask(Task task);

    List<Task> getTasks();

    Task getTaskById(Task task);

    void updateTask(Task task);

    void deleteTaskById(Task task);

    void deleteTasks();

    Epic addEpic(Epic epic);

    List<Epic> getEpics();

    Epic getEpicById(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(Epic epic);

    void deleteEpics();

    Subtask addSubTask(Subtask subtask);

    List<Subtask> getSubTask();

    List<Subtask> getSubtasksForEpic(Epic epic);

    Subtask getSubtaskById(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(Subtask subtask);

    void deleteSubtasks();

    void clear();
}
