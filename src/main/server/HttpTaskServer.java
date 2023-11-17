package main.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import main.manager.HistoryManager;
import main.manager.Managers;
import main.manager.TaskManager;
import main.server.http_handlers.EpicHandler;
import main.server.http_handlers.HistoryHandler;
import main.server.http_handlers.SubtaskByEpicHandler;
import main.server.http_handlers.SubtaskHandler;
import main.server.http_handlers.TaskHandler;
import main.server.http_handlers.TasksHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final Gson gson;

    public HttpTaskServer() throws IOException, InterruptedException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault(historyManager);
        gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", new TasksHandler(taskManager, gson));
        httpServer.createContext("/tasks/task/", new TaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/epic/", new EpicHandler(taskManager, gson));
        httpServer.createContext("/tasks/subtask/", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/subtask/epic/", new SubtaskByEpicHandler(taskManager, gson));
        httpServer.createContext("/tasks/history/", new HistoryHandler(taskManager, gson));
    }

    public void start() {
        System.out.println("HTTP server running on " + PORT + " port!");
        System.out.println("http://localhost:" + PORT + "/tasks/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("HTTP server stopped on " + PORT + " port!");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
    }
}