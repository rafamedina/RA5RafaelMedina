package org.example;

import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;

public class LibroService {

    private static final String COL = "/db/biblioteca";
    private static final String DOC = "/db/biblioteca/libros.xml";

    private void ejecutarUpdate(String xquery) throws Exception {
        Collection col = ConexionXMLDB.conectar(COL);
        try {
            XQueryService service = (XQueryService) col.getService(
                    "XQueryService", "1.0");
            service.query(xquery);
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }

    // 1. CREAR: Usamos 'update insert' (Sintaxis Legacy)
    public void crear(String id, String titulo, String autor,
                      int anio, boolean disponible) throws Exception {

        String xquery = String.format("""
            update insert 
                <libro id="%s">
                    <titulo>%s</titulo>
                    <autor>%s</autor>
                    <anio>%d</anio>
                    <disponible>%s</disponible>
                </libro>
            into doc('%s')//libros
            """, id, titulo, autor, anio, disponible, DOC);

        ejecutarUpdate(xquery);
        System.out.println("Libro creado: " + id);
    }

    // 2. ACTUALIZAR: Usamos 'update value ... with' (Sintaxis Legacy para valores)
    public void actualizar(String id, String campo,
                           String valor) throws Exception {

        // 'update value' sirve para cambiar el texto dentro de una etiqueta o atributo
        String xquery = String.format("""
            update value 
                doc('%s')//libro[@id='%s']/%s 
            with '%s'
            """, DOC, id, campo, valor);

        ejecutarUpdate(xquery);
        System.out.println("Actualizado campo '" + campo + "' del libro " + id);
    }

    // 3. ELIMINAR: Usamos 'update delete' (Sintaxis Legacy)
    public void eliminar(String id) throws Exception {
        String xquery = String.format("""
            update delete doc('%s')//libro[@id='%s']
            """, DOC, id);

        ejecutarUpdate(xquery);
        System.out.println("Eliminado libro: " + id);
    }

    // 4. PRESTAR
    public void prestar(String id) throws Exception {
        actualizar(id, "disponible", "false");
        System.out.println("-> Libro prestado: " + id);
    }

    // 5. DEVOLVER
    public void devolver(String id) throws Exception {
        actualizar(id, "disponible", "true");
        System.out.println("-> Libro devuelto: " + id);
    }

    public static void main(String[] args) {
        try {
            LibroService service = new LibroService();

            System.out.println("=== INICIANDO PRUEBAS CRUD ===");

            // 1. Crear
            service.crear("L099", "Java para Principiantes", "Pepe Programador", 2025, true);

            // 2. Prestar
            service.prestar("L099");

            // 3. Actualizar
            service.actualizar("L099", "titulo", "Java Avanzado");

            // 4. Devolver
            service.devolver("L099");

            // 5. Eliminar
            service.eliminar("L099");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}