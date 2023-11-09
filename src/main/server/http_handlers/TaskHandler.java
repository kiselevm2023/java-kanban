package main.server.http_handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.constants.StatusCode;
import main.manager.TaskManager;
import main.server.InstantAdapter;
import main.tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class TaskHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {

        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode;
        String response;
        String method = httpExchange.getRequestMethod();
        String path = String.valueOf(httpExchange.getRequestURI());

        System.out.println("Request is processing: " + path + " with method: " + method);

        switch (method) {
            case "GET":
                String query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = StatusCode.CODE_200.getCode();
                    String jsonString = gson.toJson(taskManager.getAllTasks());
                    System.out.println("GET all tasks: " + jsonString);
                    response = jsonString;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Task task = taskManager.getTaskById(id);
                        if (task != null) {
                            response = gson.toJson(task);
                        } else {
                            response = "Task is not found with id";
                        }
                        statusCode = StatusCode.CODE_200.getCode();
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Request do not have value of id";
                    } catch (NumberFormatException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Wrong format of id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Task task = gson.fromJson(bodyRequest, Task.class);
                    int id = task.getId();
                    if (taskManager.getTaskById(id) != null) {
                        taskManager.updateTask(task);
                        statusCode = StatusCode.CODE_201.getCode();
                        response = "Task with id=" + id + " updated";
                    } else {
                        Task taskCreated = taskManager.addTask(task);
                        System.out.println("Created task: " + taskCreated);
                        int idCreated = taskCreated.getId();
                        statusCode = StatusCode.CODE_201.getCode();
                        response = ("Created task with id=" + idCreated);
                        System.out.println("Created task with id=" + idCreated);
                    }
                } catch (JsonSyntaxException e) {
                    statusCode = StatusCode.CODE_400.getCode();
                    response = "Wrong format of request";
                }
                break;
            case "DELETE":
                response = "";
                query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteAllTasks();
                    statusCode = StatusCode.CODE_200.getCode();
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteTaskById(id);
                        statusCode = StatusCode.CODE_200.getCode();
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Request do not have value of id";
                    } catch (NumberFormatException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Wrong format of id";
                    }
                }
                break;
            default:
                statusCode = StatusCode.CODE_400.getCode();
                response = "Wrong request";
        }

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        httpExchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}