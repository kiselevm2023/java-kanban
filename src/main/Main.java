package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.constants.Status;
import main.server.KVServer;
import main.manager.HistoryManager;
import main.manager.Managers;
import main.manager.TaskManager;
import main.server.InstantAdapter;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;
import main.server.KVTaskClient;

import java.io.IOException;
import java.time.Instant;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer server;
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantAdapter())
                    .create();

            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault(historyManager);

            Task task1 = new Task("Task 1", "Definition Task 1", Status.NEW, Instant.now(), 30);
            httpTaskManager.addTask(task1);
            Task task2 = new Task("Task 2", "Definition Task 2", Status.NEW, Instant.now(), 30);
            httpTaskManager.addTask(task2);

            Epic epic1 = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 30);
            httpTaskManager.addEpic(epic1);

            SubTask subTask1 = new SubTask(
                    "SubTask 1", "Definition SubTask 1", Status.NEW, epic1.getId(), Instant.now(), 60);
            httpTaskManager.addSubTask(subTask1);
            SubTask subTask2 = new SubTask(
                    "SubTask 2", "Definition SubTask 2", Status.NEW, epic1.getId(), Instant.now(), 60);
            httpTaskManager.addSubTask(subTask2);
            SubTask subTask3 = new SubTask(
                    "SubTask 3", "Definition SubTask 3", Status.NEW, epic1.getId(), Instant.now(), 60);
            httpTaskManager.addSubTask(subTask3);

            httpTaskManager.getTaskById(task1.getId());
            httpTaskManager.getEpicById(epic1.getId());
            httpTaskManager.getSubTaskById(subTask1.getId());
            httpTaskManager.getSubTaskById(subTask2.getId());
            httpTaskManager.getSubTaskById(subTask3.getId());

            System.out.println("\nOutput of all tasks");
            System.out.println(gson.toJson(httpTaskManager.getAllTasks()));
            System.out.println("\nOutput of all epics");
            System.out.println(gson.toJson(httpTaskManager.getAllEpics()));
            System.out.println("\nOutput of all subtasks");
            System.out.println(gson.toJson(httpTaskManager.getAllSubtasks()));
            System.out.println("\nLoaded manager");
            System.out.println(httpTaskManager);
            server.stop();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
