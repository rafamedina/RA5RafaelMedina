package org.example;

public class CrearEstructura {
    
    public static void main(String[] args) throws Exception {
        String[] colecciones = {
            "/db/biblioteca",
            "/db/biblioteca/libros",
            "/db/biblioteca/libros/ficcion",
            "/db/biblioteca/libros/no-ficcion",
            "/db/biblioteca/libros/infantil",
            "/db/biblioteca/libros/tecnico",
            "/db/biblioteca/autores",
            "/db/biblioteca/prestamos",
            "/db/biblioteca/prestamos/activos",
            "/db/biblioteca/prestamos/historico",
            "/db/biblioteca/usuarios"
        };
        
        for (String col : colecciones) {
            String padre = col.substring(0, col.lastIndexOf('/'));
            String nombre = col.substring(col.lastIndexOf('/') + 1);
            if (!padre.isEmpty()) {
                GestorColecciones.crearColeccion(padre, nombre);
            }
        }
    }
}