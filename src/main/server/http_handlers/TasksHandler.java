package main.server.http_handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.constants.StatusCode;
import main.manager.TaskManager;
import main.server.InstantAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class TasksHandler implements HttpHandler {
    Gson gson;

    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode;
        String response;
        String method = httpExchange.getRequestMethod();
        String path = String.valueOf(httpExchange.getRequestURI());

        System.out.println("Request is processing " + path + " with method " + method);

        switch (method) {
            case "GET":
                statusCode = StatusCode.CODE_200.getCode();
                response = gson.toJson(taskManager.getPrioritizedTasks());
                break;
            default:
                response = "Wrong request";
                statusCode = StatusCode.CODE_400.getCode();
        }

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}