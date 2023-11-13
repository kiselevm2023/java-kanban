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

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    private String path;

    private  Gson gson;

    public HTTPTaskManager(HistoryManager historyManager, String path) throws IOException {
        super(historyManager);
        client = new KVTaskClient(path);
        gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
    }


    public HTTPTaskManager load (HistoryManager historyManager, String path) throws IOException {
        HTTPTaskManager httpTaskManager;

        try {
            httpTaskManager = new HTTPTaskManager( historyManager, path);
            JsonElement jsonTasks = JsonParser.parseString(client.load("tasks"));

            if (!jsonTasks.isJsonNull()) {
                JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
                for (JsonElement jsonTask : jsonTasksArray) {
                    Task task = gson.fromJson(jsonTask, Task.class);
                    this.tasks.put(task.getId(), task);
                }
            }


            JsonElement jsonEpics = JsonParser.parseString(client.load("epics"));
            if (!jsonEpics.isJsonNull()) {
                JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
                for (JsonElement jsonEpic : jsonEpicsArray) {
                    Epic task = gson.fromJson(jsonEpic, Epic.class);
                    this.epics.put(task.getId(), task);
                }
            }

            JsonElement jsonSubtasks = JsonParser.parseString(client.load("subtasks"));
            if (!jsonSubtasks.isJsonNull()) {
                JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
                for (JsonElement jsonSubtask : jsonSubtasksArray) {
                    SubTask task = gson.fromJson(jsonSubtask, SubTask.class);
                    this.subTasks.put(task.getId(), task);
                }

            }

            JsonElement jsonHistoryList = JsonParser.parseString(client.load("history"));
            if (!jsonHistoryList.isJsonNull()) {
                JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
                for (JsonElement jsonTaskId : jsonHistoryArray) {
                    int taskId = jsonTaskId.getAsInt();
                    if (this.subTasks.containsKey(taskId)) {
                        this.tasks.get(taskId);
                    } else if (this.epics.containsKey(taskId)) {
                        this.epics.get(taskId);
                    } else if (this.tasks.containsKey(taskId)) {
                        this.subTasks.get(taskId);
                    }
                }
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