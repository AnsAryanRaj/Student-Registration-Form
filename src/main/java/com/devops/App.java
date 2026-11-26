package com.devops;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight Java HTTP Web Application for Student Registration.
 * Built using the standard Java SE HTTP Server (no heavy frameworks).
 */
public class App {

    public static final int DEFAULT_PORT = 8080;
    public static final int[] CANDIDATE_PORTS = {8080, 8081, 8082, 8083, 8084};

    public static void main(String[] args) {
        int[] portsToTry = CANDIDATE_PORTS;

        // Check if an explicit port was provided via CLI argument
        if (args.length > 0) {
            try {
                portsToTry = new int[]{Integer.parseInt(args[0].trim())};
            } catch (NumberFormatException e) {
                System.err.println("Invalid port specified in arguments. Falling back to default port sequence (8080-8084).");
            }
        } else {
            // Check if PORT environment variable is set
            String envPort = System.getenv("PORT");
            if (envPort != null && !envPort.trim().isEmpty()) {
                try {
                    portsToTry = new int[]{Integer.parseInt(envPort.trim())};
                } catch (NumberFormatException ignored) {
                }
            }
        }

        HttpServer server = null;
        int selectedPort = -1;

        // Try candidate ports in sequence (8080, 8081, 8082, 8083, 8084)
        for (int p : portsToTry) {
            try {
                server = startServer(p);
                selectedPort = p;
                break;
            } catch (BindException e) {
                System.out.println("[INFO] Port " + p + " is occupied. Trying next port...");
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to bind to port " + p + ": " + e.getMessage());
            }
        }

        // If all ports are occupied, print a clear error message
        if (server == null) {
            System.err.println("=================================================");
            System.err.println(" [ERROR] Server startup failed: All ports (8080, 8081, 8082, 8083, 8084) are occupied!");
            System.err.println(" Please terminate processes occupying these ports or specify a custom port.");
            System.err.println("=================================================");
            return;
        }

        System.out.println("=================================================");
        System.out.println(" Student Registration Web App is running!");
        System.out.println(" URL: http://localhost:" + selectedPort + "/");
        System.out.println(" Health Check: http://localhost:" + selectedPort + "/health");
        System.out.println(" Press Ctrl+C to stop the server.");
        System.out.println("=================================================");
    }

    public static HttpServer startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StudentHandler());
        server.createContext("/health", exchange -> {
            byte[] response = "OK".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });
        server.setExecutor(null); // default single-threaded executor
        server.start();
        return server;
    }

    public static void stopServer(HttpServer server) {
        if (server != null) {
            server.stop(0);
        }
    }

    static class StudentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if ("POST".equalsIgnoreCase(method)) {
                handlePost(exchange);
            } else {
                handleGet(exchange);
            }
        }

        private void handleGet(HttpExchange exchange) throws IOException {
            byte[] htmlBytes;
            try (InputStream in = App.class.getResourceAsStream("/index.html")) {
                if (in == null) {
                    String fallback = "<h1>Student Registration Form</h1><p>index.html not found.</p>";
                    htmlBytes = fallback.getBytes(StandardCharsets.UTF_8);
                } else {
                    htmlBytes = in.readAllBytes();
                }
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, htmlBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(htmlBytes);
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            // Read form body
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }

            Map<String, String> formData = parseFormData(body.toString());
            String name = escapeHtml(formData.getOrDefault("name", "N/A"));
            String rollNo = escapeHtml(formData.getOrDefault("rollNo", "N/A"));
            String email = escapeHtml(formData.getOrDefault("email", "N/A"));
            String course = escapeHtml(formData.getOrDefault("course", "N/A"));

            String responseHtml = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Registration Confirmation</title>\n" +
                    "    <style>\n" +
                    "        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }\n" +
                    "        body { background: linear-gradient(135deg, #eef2f7 0%, #dbe4ee 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 20px; }\n" +
                    "        .card { background: #ffffff; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.08); width: 100%; max-width: 480px; padding: 35px 30px; text-align: center; }\n" +
                    "        .icon { font-size: 48px; color: #16a34a; margin-bottom: 12px; }\n" +
                    "        h1 { color: #15803d; font-size: 22px; margin-bottom: 8px; }\n" +
                    "        p.desc { color: #64748b; font-size: 14px; margin-bottom: 22px; }\n" +
                    "        .details { text-align: left; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 15px 18px; margin-bottom: 22px; }\n" +
                    "        .row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #f1f5f9; font-size: 14px; }\n" +
                    "        .row:last-child { border-bottom: none; }\n" +
                    "        .label { color: #64748b; font-weight: 600; }\n" +
                    "        .value { color: #1e293b; font-weight: 600; }\n" +
                    "        .btn-back { display: inline-block; width: 100%; background-color: #2563eb; color: white; padding: 12px; border-radius: 8px; font-weight: 600; text-decoration: none; transition: background-color 0.2s; }\n" +
                    "        .btn-back:hover { background-color: #1d4ed8; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"card\">\n" +
                    "        <div class=\"icon\">&#10004;</div>\n" +
                    "        <h1>Registration Successful!</h1>\n" +
                    "        <p class=\"desc\">The student details have been recorded successfully.</p>\n" +
                    "        <div class=\"details\">\n" +
                    "            <div class=\"row\"><span class=\"label\">Student Name:</span><span class=\"value\">" + name + "</span></div>\n" +
                    "            <div class=\"row\"><span class=\"label\">Roll Number:</span><span class=\"value\">" + rollNo + "</span></div>\n" +
                    "            <div class=\"row\"><span class=\"label\">Email:</span><span class=\"value\">" + email + "</span></div>\n" +
                    "            <div class=\"row\"><span class=\"label\">Course:</span><span class=\"value\">" + course + "</span></div>\n" +
                    "        </div>\n" +
                    "        <a href=\"/\" class=\"btn-back\">Register Another Student</a>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            byte[] responseBytes = responseHtml.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }

        private Map<String, String> parseFormData(String formData) {
            Map<String, String> map = new HashMap<>();
            if (formData == null || formData.isEmpty()) {
                return map;
            }
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] parts = pair.split("=", 2);
                if (parts.length == 2) {
                    String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                    String val = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                    map.put(key, val);
                } else if (parts.length == 1) {
                    String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                    map.put(key, "");
                }
            }
            return map;
        }

        private String escapeHtml(String text) {
            if (text == null) return "";
            return text.replace("&", "&amp;")
                       .replace("<", "&lt;")
                       .replace(">", "&gt;")
                       .replace("\"", "&quot;")
                       .replace("'", "&#39;");
        }
    }
}
