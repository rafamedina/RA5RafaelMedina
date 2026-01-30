package org.example.Ejercicio6;

import jakarta.xml.bind.*;
import org.example.AlmacenDocumentos;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.XMLResource;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class JAXBService {

    private final JAXBContext context;
    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;

    public JAXBService() throws JAXBException {
        // Inicializamos JAXB para que entienda nuestras clases Libro y Biblioteca
        context = JAXBContext.newInstance(Libro.class, Biblioteca.class);
        
        // El Marshaller convierte OBJETO -> XML
        marshaller = context.createMarshaller();
        // Esta línea hace que el XML salga bonito (tabulado) y no todo en una línea
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        // El Unmarshaller convierte XML -> OBJETO
        unmarshaller = context.createUnmarshaller();
    }

    // 1. Convertir OBJETO Java a TEXTO XML (Marshalling)
    public String toXML(Libro libro) throws JAXBException {
        StringWriter writer = new StringWriter();
        marshaller.marshal(libro, writer);
        return writer.toString();
    }

    // 2. Convertir TEXTO XML a OBJETO Java (Unmarshalling)
    public Libro fromXML(String xml) throws JAXBException {
        StringReader reader = new StringReader(xml);
        return (Libro) unmarshaller.unmarshal(reader);
    }

    // 3. Guardar un Libro en eXist-db
    public void guardar(String colPath, Libro libro) throws Exception {
        String xml = toXML(libro);
        // Generamos un nombre de archivo único basado en el ID, ej: libro_L100.xml
        String nombre = "libro_" + libro.getId() + ".xml";
        
        // Usamos el método nuevo que acabas de añadir
        AlmacenDocumentos.guardarDesdeString(colPath, nombre, xml);
    }

    // 4. Recuperar un Libro de eXist-db
    public Libro recuperar(String colPath, String id) throws Exception {
        String nombre = "libro_" + id + ".xml";
        String xml = AlmacenDocumentos.obtenerContenido(colPath, nombre);
        
        if (xml == null) {
            throw new Exception("Libro no encontrado: " + id);
        }
        
        return fromXML(xml);
    }

    // --- MAIN PARA PROBARLO ---
    public static void main(String[] args) {
        try {
            JAXBService service = new JAXBService();

            System.out.println("=== 1. CREANDO OBJETO JAVA ===");
            Libro libro = new Libro("L100", "Aprende JAXB", "Java Expert", 2025);
            libro.setGeneros(List.of("Programacion", "Educativo"));
            libro.setPaginas(200);
            System.out.println(libro);

            System.out.println("\n=== 2. SERIALIZANDO A XML (EN MEMORIA) ===");
            String xml = service.toXML(libro);
            System.out.println(xml);

            System.out.println("\n=== 3. GUARDANDO EN EXIST-DB ===");
            // Asegúrate de que esta carpeta existe en tu base de datos
            service.guardar("/db/biblioteca/libros", libro);

            System.out.println("\n=== 4. RECUPERANDO DE EXIST-DB Y CONVIRTIENDO A JAVA ===");
            Libro desdeBD = service.recuperar("/db/biblioteca/libros", "L100");
            System.out.println("Recuperado: " + desdeBD.getTitulo() + " de " + desdeBD.getAutor());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}