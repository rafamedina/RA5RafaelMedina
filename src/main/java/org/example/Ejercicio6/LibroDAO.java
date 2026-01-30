package org.example.Ejercicio6;

import org.example.ConexionXMLDB;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XQueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Implementacion para Libro
public class LibroDAO implements GenericXMLDAO<Libro> {
    
    private static final String COLECCION = "/db/biblioteca/libros";
    private final JAXBService jaxbService;
    
    public LibroDAO() throws Exception {
        this.jaxbService = new JAXBService();
    }
    
    @Override
    public void guardar(Libro libro) throws Exception {
        jaxbService.guardar(COLECCION, libro);
    }
    
    @Override
    public Optional<Libro> buscarPorId(String id) throws Exception {
        try {
            Libro libro = jaxbService.recuperar(COLECCION, id);
            return Optional.of(libro);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Libro> buscarTodos() throws Exception {
        String xquery = """
            for $doc in collection('%s')
            return $doc/libro
            """.formatted(COLECCION);
        
        List<Libro> libros = new ArrayList<>();
        Collection col = ConexionXMLDB.conectar(COLECCION);
        try {
            XQueryService service = (XQueryService) col.getService(
                "XQueryService", "1.0");
            ResourceSet rs = service.query(xquery);
            ResourceIterator it = rs.getIterator();
            while (it.hasMoreResources()) {
                String xml = (String) it.nextResource().getContent();
                libros.add(jaxbService.fromXML(xml));
            }
        } finally {
            ConexionXMLDB.cerrar(col);
        }
        return libros;
    }
    
    @Override
    public void actualizar(Libro libro) throws Exception {
        eliminar(libro.getId());
        guardar(libro);
    }
    
    @Override
    public void eliminar(String id) throws Exception {
        String nombre = "libro_" + id + ".xml";
        Collection col = ConexionXMLDB.conectar(COLECCION);
        try {
            Resource res = col.getResource(nombre);
            if (res != null) {
                col.removeResource(res);
            }
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }
    
    // Metodos adicionales
    public List<Libro> buscarPorAutor(String autor) throws Exception {
        String xquery = """
            for $doc in collection('%s')
            where contains($doc//autor, '%s')
            return $doc/libro
            """.formatted(COLECCION, autor);
        
        return ejecutarConsulta(xquery);
    }
    
    public List<Libro> buscarDisponibles() throws Exception {
        String xquery = """
            for $doc in collection('%s')
            where $doc//disponible = 'true'
            return $doc/libro
            """.formatted(COLECCION);
        
        return ejecutarConsulta(xquery);
    }
    
    private List<Libro> ejecutarConsulta(String xquery) 
            throws Exception {
        List<Libro> libros = new ArrayList<>();
        Collection col = ConexionXMLDB.conectar(COLECCION);
        try {
            XQueryService service = (XQueryService) col.getService(
                "XQueryService", "1.0");
            ResourceSet rs = service.query(xquery);
            ResourceIterator it = rs.getIterator();
            while (it.hasMoreResources()) {
                String xml = (String) it.nextResource().getContent();
                libros.add(jaxbService.fromXML(xml));
            }
        } finally {
            ConexionXMLDB.cerrar(col);
        }
        return libros;
    }
}
