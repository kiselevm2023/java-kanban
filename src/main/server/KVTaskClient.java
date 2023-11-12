package main.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import main.exceptions.ManagerSaveException;

public class KVTaskClient {

    private final String apiToken;
    private final String serverURL;

    public KVTaskClient(String serverURL) throws IOException, InterruptedException {
        this.serverURL = serverURL;

        URI uri = URI.create(this.serverURL + "/register");

        try {
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new ManagerSaveException ("An error occurred during registration");
            }
            apiToken = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException ("Error occurred during the request");
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(this.serverURL + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() != 200) {
                throw new ManagerSaveException ("Data not saved");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException ("Error occurred during the request");
        }
    }

    public String load(String key) {
        URI uri = URI.create(this.serverURL + "/load/" + key + "?API_TOKEN=" + apiToken);

        try {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error, Data is not founded");
                //System.out.println("Data is not founded ");
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException ("Error occurred during the request");
        }
    }
}