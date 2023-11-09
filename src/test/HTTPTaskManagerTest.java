package test;

import main.constants.Status;
import main.server.KVServer;
import main.manager.HistoryManager;
import main.manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import main.server.HTTPTaskManager;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;


import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    private KVServer server;
    HTTPTaskManager manager;

    @Override
    public HTTPTaskManager createManager() {
        try {
            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            manager = Managers.getDefault(historyManager);
        } catch (IOException | InterruptedException e) {
            System.out.println("Error of creating manager");
        }
        return manager;
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void loadTasksTest() {
        Task task1 = new Task("Task 1", "Definition Task 1", Status.NEW, Instant.now(), 5);
        Task task2 = new Task("Task 2", "Definition Task 2", Status.NEW, Instant.now(), 5);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllTasks(), list);
    }

    @Test
    public void loadEpicsTest() {
        Epic epic1 = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 10);
        Epic epic2 = new Epic("Epic 2", "Definition Epic 2", Status.NEW, Instant.now(), 10);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllEpics(), list);
    }

    @Test
    public void loadSubTasksTest() {
        Epic epic1 = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 70);
        SubTask subTask1 = new SubTask("SubTask 1", "Definition SubTask 1", Status.NEW, epic1.getId()
                , Instant.now(), 70);
        SubTask SubTask2 = new SubTask("SubTask 2", "Definition SubTask 2", Status.NEW, epic1.getId(),
                Instant.now(), 70);
        manager.addSubTask(subTask1);
        manager.addSubTask(SubTask2);
        manager.getSubTaskById(subTask1.getId());
        manager.getSubTaskById(SubTask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllSubtasks(), list);
    }
}