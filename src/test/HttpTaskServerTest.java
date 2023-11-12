package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import main.constants.Status;
import main.constants.StatusCode;
import main.server.KVServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.server.HttpTaskServer;
import main.server.InstantAdapter;
import main.tasks.Epic;
import main.tasks.SubTask;
import main.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static com.google.gson.JsonParser.parseString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    private final String BASE_PATH = "http://localhost:8080/";

    @BeforeAll
    static void startServer() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopServer() {
        kvServer.stop();
        taskServer.stop();
    }


    @BeforeEach
    void resetServer() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/task/");
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create(BASE_PATH + "tasks/epic/");
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create(BASE_PATH + "tasks/subtask/");
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/task/");
        Task task = new Task("Task 1", "Definition Task 1", Status.NEW, Instant.now(), 80);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
            System.out.println("response.body(): " + response.body());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicsTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/epic/");
        Epic epic = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 90);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
            JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSubtasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/epic/");
        Epic epic = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 95);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode(), "POST request");
            if (postResponse.statusCode() == StatusCode.CODE_201.getCode()) {
                System.out.println("postResponse.body(): " + postResponse.body());
                int epicId = Integer.parseInt(postResponse.body().split("=")[1]);
                epic.setId(epicId);
                SubTask subTask = new SubTask("SubTask 1", "Definition SubTask 1",
                        Status.NEW, epic.getId(), Instant.now(), 4);
                url = URI.create(BASE_PATH + "tasks/subtask/");

                request = HttpRequest
                        .newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
                JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
                assertEquals(1, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/task/");
        Task task = new Task("Task 1", "Definition Task 1", Status.NEW, Instant.now(), 15);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode(), "POST request");
            if (postResponse.statusCode() == StatusCode.CODE_201.getCode()) {
                System.out.println("postResponse.body()" + postResponse.body());
                int id = Integer.parseInt(postResponse.body().split("=")[1]);
                task.setId(id);
                url = URI.create(BASE_PATH + "tasks/task/" + "?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task.getId(), responseTask.getId());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/epic/");
        Epic epic = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 15);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode(), "POST request");
            if (postResponse.statusCode() == StatusCode.CODE_201.getCode()) {
                int id = Integer.parseInt(postResponse.body().split("=")[1]);
                epic.setId(id);
                url = URI.create(BASE_PATH + "tasks/epic/" + "?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);
                assertEquals(epic.getId(), responseTask.getId());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSubtaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/epic/");
        Epic epic = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 45);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode(), "POST request");
            if (postResponse.statusCode() == StatusCode.CODE_201.getCode()) {
                int epicId = Integer.parseInt(postResponse.body().split("=")[1]);
                epic.setId(epicId);
                SubTask subtask = new SubTask("SubTask 1", "Definition SubTask 1", Status.NEW, epic.getId()
                        , Instant.now(), 75);
                url = URI.create(BASE_PATH + "tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode(), "POST request");
                if (postResponse.statusCode() == StatusCode.CODE_201.getCode()) {
                    int id = Integer.parseInt(postResponse.body().split("=")[1]);
                    subtask.setId(id);
                    url = URI.create(BASE_PATH + "tasks/subtask/" + "?id=" + id);
                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
                    SubTask responseTask = gson.fromJson(response.body(), SubTask.class);
                    assertEquals(subtask.getId(), responseTask.getId());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/task/");
        Task task = new Task("Task 1", "Definition Task 1", Status.NEW, Instant.now(), 34);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteEpicsTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/epic/");
        Epic epic = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 5);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
            JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteSubTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/epic/");
        Epic epic = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 17);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode(), "POST request");
            if (postResponse.statusCode() == StatusCode.CODE_201.getCode()) {
                int epicId = Integer.parseInt(postResponse.body().split("=")[1]);
                epic.setId(epicId);
                SubTask subtask = new SubTask("SubTask 1", "Definition SubTask 1", Status.NEW, epic.getId()
                        , Instant.now(), 16);
                url = URI.create(BASE_PATH + "tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
                request = HttpRequest.newBuilder().uri(url).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
                JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
                assertEquals(0, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteTaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/task/");
        Task task = new Task("Task 1", "Definition Task 1", Status.NEW, Instant.now(), 89);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(postResponse.body().split("=")[1]);
            url = URI.create(BASE_PATH + "tasks/task/" + "?id=" + id);
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Task is not found with id", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteEpicByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/epic/");
        Epic epic = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 75);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode(), "POST request");
            if (postResponse.statusCode() == StatusCode.CODE_201.getCode()) {
                int id = Integer.parseInt(postResponse.body().split("=")[1]);
                url = URI.create(BASE_PATH + "tasks/epic/" + "?id=" + id);
                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

                request = HttpRequest.newBuilder().uri(url).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals("Epic is not found with id", response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteSubtaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "tasks/epic/");
        Epic epic = new Epic("Epic 1", "Definition Epic 1", Status.NEW, Instant.now(), 12);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode(), "POST request");
            if (postResponse.statusCode() == StatusCode.CODE_201.getCode()) {
                SubTask subtask = new SubTask("SubTask 1", "Definition SubTask 1", Status.NEW, epic.getId()
                        , Instant.now(), 12);
                url = URI.create(BASE_PATH + "tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode(), "POST request");
                if (postResponse.statusCode() == StatusCode.CODE_201.getCode()) {
                    int id = Integer.parseInt(postResponse.body().split("=")[1]);
                    subtask.setId(id);
                    url = URI.create(BASE_PATH + "tasks/subtask/" + "?id=" + id);
                    request = HttpRequest.newBuilder().uri(url).DELETE().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals("Subtask is not found with id", response.body());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}