package org.example;

import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.CollectionManagementService;



public class GestorColecciones {
    
    public static void crearColeccion(String padre, String nombre) 
            throws Exception {
        Collection col = ConexionXMLDB.conectar(padre);
        try {
            CollectionManagementService mgt = 
                (CollectionManagementService) col.getService(
                    "CollectionManagementService", "1.0");
            mgt.createCollection(nombre);
            System.out.println("Creada: " + padre + "/" + nombre);
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }
    
    public static String[] listarSubcolecciones(String path) 
            throws Exception {
        Collection col = ConexionXMLDB.conectar(path);
        try {
            return col.listChildCollections();
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }
    
    public static void eliminarColeccion(String padre, String nombre) 
            throws Exception {
        Collection col = ConexionXMLDB.conectar(padre);
        try {
            CollectionManagementService mgt = 
                (CollectionManagementService) col.getService(
                    "CollectionManagementService", "1.0");
            mgt.removeCollection(nombre);
            System.out.println("Eliminada: " + nombre);
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }
    
    public static void main(String[] args) throws Exception {
        // Crear estructura
        crearColeccion("/db", "biblioteca");
        crearColeccion("/db/biblioteca", "libros");
        crearColeccion("/db/biblioteca", "autores");
        
        // Listar
        String[] subs = listarSubcolecciones("/db/biblioteca");
        System.out.println("Subcolecciones:");
        for (String s : subs) {
            System.out.println("  - " + s);
        }
    }
}