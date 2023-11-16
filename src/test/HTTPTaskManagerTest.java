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
        HTTPTaskManager restoredHttpTaskManager1 = manager.load();
        assertEquals(manager.getAllTasks().toArray(), restoredHttpTaskManager1.getAllTasks().toArray());
        assertNotNull(restoredHttpTaskManager1.getAllTasks().toArray(), "Список задач не восстановился");
        } catch (IOException e) {
            System.out.println("Error of creating manager");
        }
    }

    @Test
    public void loadEpicsTest() {
        Epic epic1 = new Epic("Title Epic 1", "Description Epic 1", Status.NEW, Instant.now(), 15);
        Epic epic2 = new Epic("Title Epic 2", "Description Epic 2", Status.NEW, Instant.now(), 15);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Task epic1Id = manager.getEpicById(epic1.getId());
        Task epic2Id = manager.getEpicById(epic2.getId());
        manager.getHistory().add(epic1);
        manager.getHistory().add(epic2);
        manager.save();

        try {
            HTTPTaskManager restoredHttpTaskManager2 = manager.load();
            assertEquals(manager.getAllEpics(), restoredHttpTaskManager2.getAllEpics());
            assertNotNull(restoredHttpTaskManager2.getAllEpics().toArray(), "Список задач не восстановился");
        } catch (IOException e) {
            System.out.println("Error of creating manager");
        }
    }

    @Test
    public void loadSubTasksTest() {
        Epic epic1 = new Epic("Title Epic 1", "Description Epic 1", Status.NEW, Instant.now(), 30);
        SubTask subTask1 = new SubTask("Title SubTask 1", "Description SubTask 1", Status.NEW, epic1.getId()
                , Instant.now(), 15);
        SubTask SubTask2 = new SubTask("Title SubTask 2", "Description SubTask 2", Status.NEW, epic1.getId(),
                Instant.now(), 15);
        manager.addSubTask(subTask1);
        manager.addSubTask(SubTask2);

        SubTask SubTask1Id = manager.getSubTaskById(subTask1.getId());
        SubTask SubTask2Id = manager.getSubTaskById(SubTask2.getId());
        manager.getHistory().add(subTask1);
        manager.getHistory().add(SubTask2);
        manager.save();
        try {
            HTTPTaskManager restoredHttpTaskManager3 = manager.load();
            assertEquals(manager.getAllSubtasks(), restoredHttpTaskManager3.getAllSubtasks());
            assertNotNull(restoredHttpTaskManager3.getAllSubtasks().toArray(), "Список задач не восстановился");
        } catch (IOException e) {
            System.out.println("Error of creating manager");
        }
    }
}