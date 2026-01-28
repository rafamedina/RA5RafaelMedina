package org.example;

import java.net.http.*;
import java.net.URI;
import java.util.Base64;
import java.time.Duration;

public class ExistRESTClient {
    
    private final String baseUrl;
    private final HttpClient client;
    private final String authHeader;
    
    public ExistRESTClient(String host, int port, 
            String user, String password) {
        this.baseUrl = String.format(
            "http://%s:%d/exist/rest", host, port);
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        String creds = user + ":" + password;
        this.authHeader = "Basic " + Base64.getEncoder()
            .encodeToString(creds.getBytes());
    }
    
    public String get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", authHeader)
            .GET()
            .build();
        
        HttpResponse<String> response = client.send(
            request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return response.body();
        }
        throw new Exception("Error " + response.statusCode());
    }
    
    public void put(String path, String xml) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", authHeader)
            .header("Content-Type", "application/xml")
            .PUT(HttpRequest.BodyPublishers.ofString(xml))
            .build();
        
        HttpResponse<String> response = client.send(
            request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 201 && 
            response.statusCode() != 200) {
            throw new Exception("Error " + response.statusCode());
        }
    }
    
    public boolean delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", authHeader)
            .DELETE()
            .build();
        
        HttpResponse<String> response = client.send(
            request, HttpResponse.BodyHandlers.ofString());
        
        return response.statusCode() == 200;
    }
    
    public static void main(String[] args) throws Exception {
        ExistRESTClient client = new ExistRESTClient(
            "localhost", 8080, "admin", "");
        
        // Crear documento
        client.put("/db/test.xml", "<test>Hello</test>");
        System.out.println("Documento creado");
        
        // Leer documento
        String content = client.get("/db/test.xml");
        System.out.println("Contenido: " + content);
        
        // Eliminar documento
        client.delete("/db/test.xml");
        System.out.println("Documento eliminado");
    }
}