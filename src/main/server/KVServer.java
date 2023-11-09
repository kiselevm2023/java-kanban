package main.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import main.constants.StatusCode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange httpExchange) {

        try (httpExchange) {
            System.out.println("\n/load");
            if (!hasAuth(httpExchange)) {
                System.out.println("The request is not authorized, API-key need");
                httpExchange.sendResponseHeaders(StatusCode.CODE_403.getCode(), 0);
                return;
            }
            if ("GET".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key is empty. Key indicate: /load/{key}");
                    httpExchange.sendResponseHeaders(StatusCode.CODE_400.getCode(), 0);
                    return;
                }
                if (data.get(key) == null) {
                    System.out.println("No information for '" + key + "', data is empty");
                    httpExchange.sendResponseHeaders(StatusCode.CODE_404.getCode(), 0);
                    return;
                }
                String response = data.get(key);
                sendText(httpExchange, response);
                System.out.println("Value of key " + key + " sent for request");
                httpExchange.sendResponseHeaders(StatusCode.CODE_200.getCode(), 0);
            } else {
                System.out.println("/load wait GET-request, but received: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(StatusCode.CODE_405.getCode(), 0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            System.out.println("\n/save");
            if (!hasAuth(httpExchange)) {
                System.out.println("The request is not authorized, API-key need");
                httpExchange.sendResponseHeaders(StatusCode.CODE_403.getCode(), 0);
                return;
            }
            if ("POST".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key is empty. key indicate: /save/{key}");
                    httpExchange.sendResponseHeaders(StatusCode.CODE_400.getCode(), 0);
                    return;
                }
                String value = readText(httpExchange);
                if (value.isEmpty()) {
                    System.out.println("Value is empty. value indicate in request");
                    httpExchange.sendResponseHeaders(StatusCode.CODE_400.getCode(), 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Value for key " + key + " is updated");
                httpExchange.sendResponseHeaders(StatusCode.CODE_200.getCode(), 0);
            } else {
                System.out.println("/save wait POST-request, but received: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(StatusCode.CODE_405.getCode(), 0);
            }
        }
    }

    private void register(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            System.out.println("\n/register");
            if ("GET".equals(httpExchange.getRequestMethod())) {
                sendText(httpExchange, apiToken);
            } else {
                System.out.println("/register wait GET-request, but received " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(StatusCode.CODE_405.getCode(), 0);
            }
        }
    }

    public void start() {
        System.out.println("We start the server on the port" + PORT);
        System.out.println("Open in browser http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println(PORT + " port of server is stopped");
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange httpExchange) {
        String rawQuery = httpExchange.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(StatusCode.CODE_200.getCode(), resp.length);
        httpExchange.getResponseBody().write(resp);
    }
}