package org.example;

import java.io.File;
import java.nio.file.*;

public class CargaMasiva {
    
    public static int cargarDirectorio(String dirLocal, 
            String colPath) throws Exception {
        
        File dir = new File(dirLocal);
        if (!dir.isDirectory()) {
            throw new Exception("No es un directorio: " + dirLocal);
        }
        
        File[] ficheros = dir.listFiles(
            (d, name) -> name.endsWith(".xml"));
        
        if (ficheros == null || ficheros.length == 0) {
            System.out.println("No hay ficheros XML");
            return 0;
        }
        
        int total = ficheros.length;
        int cargados = 0;
        int errores = 0;
        
        System.out.println("Cargando " + total + " ficheros...");
        
        for (File fichero : ficheros) {
            try {
                AlmacenDocumentos.guardarDesdeFichero(
                    colPath, fichero.getAbsolutePath());
                cargados++;
                
                // Mostrar progreso
                int pct = (cargados * 100) / total;
                System.out.printf("\rProgreso: %d%% (%d/%d)", 
                    pct, cargados, total);
                    
            } catch (Exception e) {
                errores++;
                System.err.println("\nError en " + 
                    fichero.getName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("\n\nResumen:");
        System.out.println("  Cargados: " + cargados);
        System.out.println("  Errores: " + errores);
        
        return cargados;
    }
    
    public static void main(String[] args) throws Exception {
        cargarDirectorio("./datos/libros",
            "/db/biblioteca/libros/ficcion");
    }
}