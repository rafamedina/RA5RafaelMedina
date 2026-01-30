package org.example.Ejercicio6;

import java.util.Scanner;
import java.util.List;

public class BibliotecaApp {
    
    private final LibroDAO libroDAO;
    private final Scanner scanner;
    
    public BibliotecaApp() throws Exception {
        this.libroDAO = new LibroDAO();
        this.scanner = new Scanner(System.in);
    }
    
    public void ejecutar() {
        boolean continuar = true;
        
        while (continuar) {
            mostrarMenu();
            int opcion = leerEntero("Seleccione opcion: ");
            
            try {
                switch (opcion) {
                    case 1 -> listarLibros();
                    case 2 -> buscarLibro();
                    case 3 -> crearLibro();
                    case 4 -> actualizarLibro();
                    case 5 -> eliminarLibro();
                    case 6 -> buscarPorAutor();
                    case 7 -> mostrarDisponibles();
                    case 0 -> continuar = false;
                    default -> System.out.println(
                        "Opcion no valida");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            
            if (continuar) {
                System.out.println("\nPulse Enter para continuar...");
                scanner.nextLine();
            }
        }
        
        System.out.println("Hasta pronto!");
    }
    
    private void mostrarMenu() {
        System.out.println("\n=== BIBLIOTECA XML ===");
        System.out.println("1. Listar todos los libros");
        System.out.println("2. Buscar libro por ID");
        System.out.println("3. Crear nuevo libro");
        System.out.println("4. Actualizar libro");
        System.out.println("5. Eliminar libro");
        System.out.println("6. Buscar por autor");
        System.out.println("7. Mostrar disponibles");
        System.out.println("0. Salir");
        System.out.println("======================");
    }
    
    private void listarLibros() throws Exception {
        List<Libro> libros = libroDAO.buscarTodos();
        System.out.println("\n--- Libros ("+libros.size()+") ---");
        libros.forEach(System.out::println);
    }
    
    private void buscarLibro() throws Exception {
        String id = leerTexto("ID del libro: ");
        libroDAO.buscarPorId(id)
            .ifPresentOrElse(
                libro -> {
                    System.out.println("\nEncontrado:");
                    System.out.println(libro);
                },
                () -> System.out.println("No encontrado")
            );
    }
    
    private void crearLibro() throws Exception {
        System.out.println("\n--- Nuevo Libro ---");
        String id = leerTexto("ID: ");
        String titulo = leerTexto("Titulo: ");
        String autor = leerTexto("Autor: ");
        int anio = leerEntero("Ano: ");
        
        Libro libro = new Libro(id, titulo, autor, anio);
        libroDAO.guardar(libro);
        System.out.println("Libro creado correctamente");
    }
    
    private void actualizarLibro() throws Exception {
        String id = leerTexto("ID del libro a actualizar: ");
        
        libroDAO.buscarPorId(id).ifPresentOrElse(
            libro -> {
                try {
                    System.out.println("Libro actual: " + libro);
                    String titulo = leerTexto(
                        "Nuevo titulo (enter para mantener): ");
                    if (!titulo.isEmpty()) {
                        libro.setTitulo(titulo);
                    }
                    libroDAO.actualizar(libro);
                    System.out.println("Actualizado correctamente");
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            },
            () -> System.out.println("No encontrado")
        );
    }
    
    private void eliminarLibro() throws Exception {
        String id = leerTexto("ID del libro a eliminar: ");
        String confirmar = leerTexto(
            "Esta seguro? (s/n): ");
        
        if ("s".equalsIgnoreCase(confirmar)) {
            libroDAO.eliminar(id);
            System.out.println("Eliminado correctamente");
        } else {
            System.out.println("Operacion cancelada");
        }
    }
    
    private void buscarPorAutor() throws Exception {
        String autor = leerTexto("Autor: ");
        List<Libro> libros = libroDAO.buscarPorAutor(autor);
        System.out.println("\nLibros de '" + autor + "':");
        libros.forEach(System.out::println);
    }
    
    private void mostrarDisponibles() throws Exception {
        List<Libro> libros = libroDAO.buscarDisponibles();
        System.out.println("\nLibros disponibles:");
        libros.forEach(System.out::println);
    }
    
    private String leerTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int leerEntero(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int valor = Integer.parseInt(scanner.nextLine().trim());
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Introduzca un numero valido");
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            new BibliotecaApp().ejecutar();
        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}