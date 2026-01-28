package org.example;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.CollectionManagementService;
import java.net.ConnectException;

/**
 * Clase principal para gestionar la conexión con eXist-db.
 * Implementa manejo de errores robusto (Ejercicio 6).
 */
public class ConexionXMLDB {
    
    // Constantes de configuración
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private static final String USER = "admin";
    private static final String PASSWORD = ""; // Cambia esto si tienes contraseña
    
    private static boolean driverRegistrado = false;
    
    /**
     * Registra el driver XML:DB. Solo se necesita hacer una vez.
     * @throws XMLDBConnectionException Si falla la carga del driver.
     */
    public static synchronized void registrarDriver() throws XMLDBConnectionException {
        if (!driverRegistrado) {
            try {
                Class<?> cl = Class.forName(DRIVER);
                Database database = (Database) cl.getDeclaredConstructor().newInstance();
                DatabaseManager.registerDatabase(database);
                driverRegistrado = true;
                System.out.println("[INFO] Driver registrado correctamente.");
            } catch (Exception e) {
                throw new XMLDBConnectionException("Error al registrar el driver de eXist-db", e);
            }
        }
    }
    
    /**
     * Intenta establecer conexión con una colección específica.
     * Analiza los errores para lanzar excepciones descriptivas.
     * * @param coleccion Ruta de la colección (ej: "/db")
     * @return Objeto Collection si tiene éxito
     * @throws XMLDBNotFoundException Si la colección no existe
     * @throws XMLDBAuthException Si el usuario/pass son incorrectos
     * @throws XMLDBConnectionException Si el servidor está caído u otro error técnico
     */
    public static Collection conectar(String coleccion) throws XMLDBConnectionException {
        registrarDriver();
        String fullUri = URI + coleccion;

        try {
            Collection col = DatabaseManager.getCollection(fullUri, USER, PASSWORD);
            if (col == null) {
                throw new XMLDBNotFoundException(coleccion);
            }
            return col;

        } catch (XMLDBException e) {
            // --- AÑADE ESTE BLOQUE ---
            // Si el mensaje del error dice "not found", lanzamos nuestra excepción personalizada
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new XMLDBNotFoundException(coleccion);
            }
            // -------------------------

            if (e.errorCode == ErrorCodes.PERMISSION_DENIED) {
                throw new XMLDBAuthException("Acceso denegado: Usuario o contraseña incorrectos.");
            }

            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                throw new XMLDBConnectionException("Servidor NO disponible (¿Está arrancado eXist-db?)", e);
            }

            throw new XMLDBConnectionException("Error técnico en la conexión XML:DB", e);
        } catch (Exception e) {
            throw new XMLDBConnectionException("Error inesperado al conectar", e);
        }
    }
    
    /**
     * Cierra la conexión de forma segura.
     * @param col La colección a cerrar.
     */
    public static void cerrar(Collection col) {
        if (col != null) {
            try {
                col.close();
            } catch (XMLDBException e) {
                System.err.println("[WARN] No se pudo cerrar la colección: " + e.getMessage());
            }
        }
    }
    
    // ==========================================
    // MÉTODO MAIN: PRUEBAS DE ERRORES (Ejercicio 6)
    // ==========================================
    public static void main(String[] args) {
        Collection col = null;

        System.out.println("--- PRUEBA 1: Conexión Exitosa ---");
        try {
            col = conectar("/db");
            System.out.println("¡ÉXITO! Conectado a: " + col.getName());
            cerrar(col);
        } catch (XMLDBConnectionException | XMLDBException e) {
            System.err.println("Fallo: " + e.getMessage());
        }

        System.out.println("\n--- PRUEBA 2: Colección No Encontrada ---");
        try {
            col = conectar("/db/esta_coleccion_no_existe");
        } catch (XMLDBNotFoundException e) {
            System.out.println("CORRECTO: Se capturó el error -> " + e.getMessage());
        } catch (XMLDBConnectionException e) {
            System.err.println("Error incorrecto: " + e.getClass().getSimpleName());
        }

        System.out.println("\n--- PRUEBA 3: Simular Servidor Caído o Auth ---");
        // Nota: Para probar Auth, cambia la contraseña arriba a una incorrecta.
        // Para probar Server Down, apaga eXist y ejecuta esto.
        try {
            col = conectar("/db");
            cerrar(col);
        } catch (XMLDBAuthException e) {
             System.err.println("Error de Autenticación: " + e.getMessage());
        } catch (XMLDBConnectionException e) {
             System.err.println("Error de Conexión: " + e.getMessage());
        }
    }
}

// =========================================================
// EXCEPCIONES PERSONALIZADAS (Pueden ir en archivos aparte)
// =========================================================

/** Excepción padre para todos los errores de conexión */
class XMLDBConnectionException extends Exception {
    public XMLDBConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
    public XMLDBConnectionException(String message) {
        super(message);
    }
}

/** Excepción específica para fallos de usuario/contraseña */
class XMLDBAuthException extends XMLDBConnectionException {
    public XMLDBAuthException(String message) {
        super(message);
    }
}

/** Excepción específica para cuando la colección no existe */
class XMLDBNotFoundException extends XMLDBConnectionException {
    public XMLDBNotFoundException(String path) {
        super("La colección solicitada no existe: " + path);
    }
}