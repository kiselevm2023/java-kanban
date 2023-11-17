package main.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import main.exceptions.ManagerSaveException;
import main.manager.FileBackedTasksManager;
import main.manager.HistoryManager;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;
import main.manager.Managers;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    private String path;

    private  Gson gson;
    HistoryManager historyManager;

    public HTTPTaskManager(HistoryManager historyManager, String path) throws IOException {
        super(historyManager);
        this.path = path;
        client = new KVTaskClient(path);
        gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
    }


    public HTTPTaskManager load () throws IOException {
        HTTPTaskManager httpTaskManager;
        Map<Integer, Task> allTasks = new HashMap<>();
        try {
            httpTaskManager = new HTTPTaskManager(Managers.getDefaultHistory(), path);
            JsonElement jsonTasks = JsonParser.parseString(client.load("tasks"));
            if (jsonTasks.isJsonNull()) {
                return null;
            }
                if (!jsonTasks.isJsonNull()) {
                    JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
                    for (JsonElement jsonTask : jsonTasksArray) {
                        Task task = gson.fromJson(jsonTask, Task.class);
                        allTasks.put(task.getId(), task);
                        httpTaskManager.tasks.put(task.getId(), task);
                        httpTaskManager.prioritizedTasks.add(task);
                    }
                }


            JsonElement jsonEpics = JsonParser.parseString(client.load("epics"));
            if (!jsonEpics.isJsonNull()) {
                JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
                for (JsonElement jsonEpic : jsonEpicsArray) {
                    Epic task = gson.fromJson(jsonEpic, Epic.class);
                    allTasks.put(task.getId(), task);
                    httpTaskManager.epics.put(task.getId(), task);
                    httpTaskManager.prioritizedTasks.add(task);
                }
            }

            JsonElement jsonSubtasks = JsonParser.parseString(client.load("subtasks"));
            if (!jsonSubtasks.isJsonNull()) {
                JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
                for (JsonElement jsonSubtask : jsonSubtasksArray) {
                    SubTask task = gson.fromJson(jsonSubtask, SubTask.class);
                    allTasks.put(task.getId(), task);
                    httpTaskManager.subTasks.put(task.getId(), task);
                    httpTaskManager.prioritizedTasks.add(task);
                }
            }

            JsonElement jsonHistoryList = JsonParser.parseString(client.load("history"));
            if (!jsonHistoryList.isJsonNull()) {
                JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
                for (JsonElement jsonTaskId : jsonHistoryArray) {
                    int taskId = jsonTaskId.getAsInt();
                    if (this.subTasks.containsKey(taskId)) {
                        SubTask task = subTasks.get(taskId);
                        httpTaskManager.getHistory().add(task);
                    } else if (this.epics.containsKey(taskId)) {
                        Epic task = epics.get(taskId);
                        httpTaskManager.getHistory().add(task);
                    } else if (this.tasks.containsKey(taskId)) {
                        Task task = tasks.get(taskId);
                        httpTaskManager.getHistory().add(task);
                    }
                }
            }
            if (!allTasks.isEmpty()) {
                int maxId = 0;
                for (int id : allTasks.keySet()) {
                    if (id > maxId) {
                        maxId = id;
                    }
                }
                httpTaskManager.getIdCounter = maxId;
            }
        } catch (ManagerSaveException e) {
            System.out.println("Error occurred during the request");
            e.printStackTrace();
            throw new ManagerSaveException ("Error occurred during the request");
        }
        return httpTaskManager;
    }

    @Override
    public void save() {
        client.put("tasks", gson.toJson(tasks.values()));
        client.put("subtasks", gson.toJson(subTasks.values()));
        client.put("epics", gson.toJson(epics.values()));
        client.put("history", gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }
}