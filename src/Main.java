import manager.TaskManager;
import manager.Managers;
import tasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager taskmanager = Managers.getDefault();

        System.out.println("Получение списка всех задач");
        Task task1=new Task("Exercise 1", "Definition 1");
        Task task2=new Task("Exercise 2", "Definition 2");
        taskmanager.addTask(task1);
        taskmanager.addTask(task2);
        Epic epic1=new Epic("Epik 1","Definition epik 1");
        taskmanager.addEpic(epic1);
        Epic epic2=new Epic("Epik 2","Definition epik 2");
        taskmanager.addEpic(epic2);
        Subtask subtask =new Subtask("Subproblem 1, Epik 1", "Definition subproblem 1", epic1.getId());
        taskmanager.addSubTask(subtask);
        Subtask subtask1 =new Subtask("Subproblem 2, Epik 1", "Definition subproblem 2", epic1.getId());
        taskmanager.addSubTask(subtask1);
        Subtask subtask2 =new Subtask("Subproblem 1, Epik 2", "Definition subproblem 1", epic1.getId());
        taskmanager.addSubTask(subtask2);

        System.out.println("Первое получение задач");
        taskmanager.getTaskById(task2);
        taskmanager.getSubtaskById(subtask1);
        taskmanager.getTaskById(task1);
        taskmanager.getSubtaskById(subtask2);
        taskmanager.getEpicById(epic2);
        taskmanager.getTaskById(task2);
        taskmanager.getSubtaskById(subtask);
        taskmanager.getEpicById(epic1);
        taskmanager.getSubtaskById(subtask1);
        taskmanager.getSubtaskById(subtask);
        System.out.println(taskmanager.getHistory());

        System.out.println("Второе получение задач");
        taskmanager.clear();
        taskmanager.getEpicById(epic2);
        taskmanager.getSubtaskById(subtask);
        taskmanager.getSubtaskById(subtask1);
        taskmanager.getTaskById(task2);
        taskmanager.getEpicById(epic1);
        taskmanager.getTaskById(task2);
        taskmanager.getTaskById(task1);
        taskmanager.getEpicById(epic2);
        taskmanager.getSubtaskById(subtask2);
        System.out.println(taskmanager.getHistory());

        System.out.println("Третье получение задач");
        taskmanager.clear();
        taskmanager.getSubtaskById(subtask);
        taskmanager.getTaskById(task1);
        taskmanager.getSubtaskById(subtask2);
        taskmanager.getEpicById(epic1);
        taskmanager.getSubtaskById(subtask1);
        taskmanager.getEpicById(epic2);
        taskmanager.getSubtaskById(subtask);
        taskmanager.getTaskById(task2);
        taskmanager.getSubtaskById(subtask1);
        taskmanager.getEpicById(epic2);
        taskmanager.getTaskById(task1);
        taskmanager.getSubtaskById(subtask);
        taskmanager.getEpicById(epic1);
        System.out.println(taskmanager.getHistory());

        System.out.println("Четвертое получение задач после удаления задачи");
        taskmanager.deleteTaskById(task1);
        System.out.println(taskmanager.getHistory());

        System.out.println("Пятое получение задач после удаления эпика");
        taskmanager.deleteEpicById(epic1);
        System.out.println(taskmanager.getHistory());

    }
}
