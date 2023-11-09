package main.manager;

import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;

import java.util.List;

public interface TaskManager {

    Task addTask(Task task);

    SubTask addSubTask(SubTask subTask);

    Epic addEpic(Epic epic);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    void deleteAllSubtasksByEpic(Epic epic);

    List<Epic> getAllEpics();

    List<Task> getAllTasks();

    List<SubTask> getAllSubtasks();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void updateStatusEpic(Epic epic);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    List<SubTask> getAllSubtasksByEpicId(int id);

    void remove(int id);

    void printTasks();

    void printEpics();

    void printSubtasks();

    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
}