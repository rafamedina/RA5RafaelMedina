package org.example.backups;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

public class BackupRestauracion {

    // Configuración
    private static final String HOST = "http://localhost:8080/exist/rest";
    private static final String USER = "admin";
    private static final String PASS = ""; // Contraseña vacía por defecto
    
    // Cliente HTTP (Nativo de Java 11+)
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        try {
            // 1. EJEMPLO DE BACKUP (Exportar de la BD a tu PC)
            System.out.println("=== INICIANDO BACKUP ===");
            hacerBackup("/db/biblioteca/libros", "./backups");

            // 2. EJEMPLO DE RESTAURACIÓN (Importar de tu PC a la BD)
            // Descomenta la siguiente línea si quieres probar la restauración:
            // System.out.println("\n=== INICIANDO RESTAURACIÓN ===");
            // restaurar("./backups", "/db/biblioteca/libros_restaurados");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- LÓGICA DE BACKUP ---
    public static void hacerBackup(String colRemota, String dirLocal) throws Exception {
        Files.createDirectories(Paths.get(dirLocal));

        // 1. Obtenemos la lista de recursos (XMLs) de la colección
        // Truco: Al pedir la colección sin especificar archivo, eXist devuelve un XML con la lista
        String urlColeccion = HOST + colRemota;
        String xmlListado = enviarPeticion(urlColeccion, "GET", null);

        // 2. Buscamos nombres de archivos en el XML de respuesta (parseo simple)
        // Buscamos patrones como name="libro1.xml"
        // NOTA: En un caso real usaríamos un parser XML, esto es simplificado para el ejercicio
        String[] lineas = xmlListado.split("\n");
        int cont = 0;

        for (String linea : lineas) {
            if (linea.contains("name=") && linea.contains(".xml")) {
                // Extraer nombre del archivo (un poco manual pero efectivo para el ejercicio)
                int start = linea.indexOf("name=\"") + 6;
                int end = linea.indexOf("\"", start);
                String nombreArchivo = linea.substring(start, end);

                // 3. Descargar el archivo
                System.out.println("Descargando: " + nombreArchivo);
                String contenido = enviarPeticion(urlColeccion + "/" + nombreArchivo, "GET", null);
                
                // 4. Guardar en disco
                Files.writeString(Paths.get(dirLocal, nombreArchivo), contenido);
                cont++;
            }
        }
        System.out.println("Backup finalizado. Archivos guardados: " + cont);
    }

    // --- LÓGICA DE RESTAURACIÓN ---
    public static void restaurar(String dirLocal, String colRemota) throws IOException {
        Path directorio = Paths.get(dirLocal);
        
        if (!Files.exists(directorio)) {
            System.out.println("El directorio de backup no existe.");
            return;
        }

        try (Stream<Path> ficheros = Files.list(directorio)) {
            ficheros.filter(p -> p.toString().endsWith(".xml")).forEach(path -> {
                try {
                    String fileName = path.getFileName().toString();
                    System.out.println("Subiendo: " + fileName);
                    
                    // Leer contenido local
                    String contenido = Files.readString(path);
                    
                    // Subir a la base de datos (PUT)
                    enviarPeticion(HOST + colRemota + "/" + fileName, "PUT", contenido);
                    
                } catch (Exception e) {
                    System.err.println("Error subiendo " + path + ": " + e.getMessage());
                }
            });
        }
        System.out.println("Restauración completada.");
    }

    // --- MÉTODO AUXILIAR PARA ENVIAR PETICIONES HTTP ---
    private static String enviarPeticion(String url, String metodo, String cuerpo) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url));

        // Autenticación Básica
        String auth = USER + ":" + PASS;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        builder.header("Authorization", "Basic " + encodedAuth);

        // Configurar método (GET o PUT)
        if ("PUT".equals(metodo)) {
            builder.PUT(HttpRequest.BodyPublishers.ofString(cuerpo));
            builder.header("Content-Type", "application/xml");
        } else {
            builder.GET();
        }

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new Exception("Error HTTP " + response.statusCode() + " en " + url);
        }

        return response.body();
    }
}