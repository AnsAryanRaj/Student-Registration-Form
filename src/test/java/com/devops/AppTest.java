package com.devops;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    private static HttpServer server;
    private static int testPort;
    private static HttpClient client;

    @BeforeAll
    public static void setUp() throws IOException {
        // Find an open port
        try (ServerSocket socket = new ServerSocket(0)) {
            testPort = socket.getLocalPort();
        }

        server = App.startServer(testPort);
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    public static void tearDown() {
        if (server != null) {
            App.stopServer(server);
        }
    }

    @Test
    public void testHealthEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + testPort + "/health"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());
    }

    @Test
    public void testGetRegistrationForm() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + testPort + "/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Student Registration"));
        assertTrue(response.body().contains("Roll Number"));
        assertTrue(response.body().contains("Register Student"));
    }

    @Test
    public void testPostRegistrationForm() throws Exception {
        String formData = "name=Alice+Smith&rollNo=CS2026&email=alice%40example.com&course=Computer+Science";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + testPort + "/"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Registration Successful!"));
        assertTrue(response.body().contains("Alice Smith"));
        assertTrue(response.body().contains("CS2026"));
    }
}
