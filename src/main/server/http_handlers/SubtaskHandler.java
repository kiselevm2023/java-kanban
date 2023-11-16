package main.server.http_handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.constants.StatusCode;
import main.manager.TaskManager;
import main.server.InstantAdapter;
import main.tasks.SubTask;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class SubtaskHandler implements HttpHandler {
    private final Gson gson;

    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode= StatusCode.CODE_400.getCode();
        String response = "Wrong request";

        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = StatusCode.CODE_200.getCode();
                    response = gson.toJson(taskManager.getAllSubtasks());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        SubTask subtask = taskManager.getSubTaskById(id);
                        if (subtask != null) {
                            response = gson.toJson(subtask);
                        } else {
                            response = "Subtask is not found with id";
                            statusCode = StatusCode.CODE_404.getCode();
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
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    if (!bodyRequest.isEmpty()) {
                        SubTask subtask = gson.fromJson(bodyRequest, SubTask.class);
                        int id = subtask.getId();
                        if (taskManager.getSubTaskById(id) != null) {
                            taskManager.updateTask(subtask);
                            statusCode = StatusCode.CODE_200.getCode();
                            response = "Subtask with id=" + id + " is updated";
                        } else {
                            System.out.println("Created");
                            SubTask subtaskCreated = taskManager.addSubTask(subtask);
                            System.out.println("Created subtask: " + subtaskCreated);
                            int idCreated = subtaskCreated.getId();
                            statusCode = StatusCode.CODE_201.getCode();
                            response = ("Created subtask with id=" + idCreated);
                            System.out.println("Created subtask with id=" + idCreated);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    response = "Wrong format of request";
                    statusCode = StatusCode.CODE_400.getCode();
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteAllSubtasks();
                    statusCode = StatusCode.CODE_200.getCode();
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteSubtaskById(id);
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

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}