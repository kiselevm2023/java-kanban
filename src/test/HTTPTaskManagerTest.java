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
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        Task task1Id = manager.getTaskById(task1.getId());
        Task task2Id = manager.getTaskById(task2.getId());
        manager.getHistory().add(task1);
        manager.getHistory().add(task2);
        manager.save();

        try {
        HTTPTaskManager restoredHttpTaskManager = manager.load();
        assertEquals(manager.getAllTasks(), restoredHttpTaskManager.getAllTasks());
        assertNotNull(restoredHttpTaskManager .getAllTasks().toArray(), "Список подзадач не восстановился");
        } catch (IOException e) {
            System.out.println("Error of creating manager");
        }
    }
}