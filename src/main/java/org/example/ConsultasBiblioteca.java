package org.example;

import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultasBiblioteca {

    private static final String COLECCION = "/db/biblioteca";

    // HEMOS CAMBIADO ESTE METODO: Ahora devuelve la List<String> directamente
    private List<String> ejecutarXQuery(String xquery) throws Exception {

        Collection col = ConexionXMLDB.conectar(COLECCION);
        List<String> resultados = new ArrayList<>();

        try {
            XQueryService service = (XQueryService) col.getService(
                    "XQueryService", "1.0");

            ResourceSet rs = service.query(xquery);
            ResourceIterator it = rs.getIterator();

            // Procesamos los datos MIENTRAS la conexión sigue abierta
            while (it.hasMoreResources()) {
                Resource res = it.nextResource();
                resultados.add((String) res.getContent());
            }

        } finally {
            // Ahora sí podemos cerrar, porque ya tenemos los datos en la lista 'resultados'
            ConexionXMLDB.cerrar(col);
        }

        return resultados;
    }

    // YA NO NECESITAMOS EL MÉTODO resultadoALista POR SEPARADO

    // 1. Buscar por titulo
    public List<String> buscarPorTitulo(String termino) throws Exception {
        String xquery = String.format("""
            for $libro in //libro
            where contains(lower-case($libro/titulo), '%s')
            return 
                <resultado>
                    <titulo>{$libro/titulo/text()}</titulo>
                    <autor>{$libro/autor/text()}</autor>
                </resultado>
            """, termino.toLowerCase());

        return ejecutarXQuery(xquery);
    }

    // 2. Buscar por genero
    public List<String> buscarPorGenero(String genero) throws Exception {
        String xquery = String.format("""
            for $libro in //libro[generos/genero='%s']
            order by $libro/titulo
            return $libro/titulo/text()
            """, genero);

        return ejecutarXQuery(xquery);
    }

    // 3. Estadisticas
    public String obtenerEstadisticas() throws Exception {
        String xquery = """
            <estadisticas>
                <total>{count(//libro)}</total>
                <disponibles>
                    {count(//libro[disponible='true'])}
                </disponibles>
                <promedio-paginas>
                    {round(avg(//libro/paginas))}
                </promedio-paginas>
            </estadisticas>
            """;

        List<String> res = ejecutarXQuery(xquery);
        return res.isEmpty() ? "" : res.get(0);
    }

    // 4. Libros disponibles
    public List<String> librosDisponibles() throws Exception {
        String xquery = """
            for $libro in //libro[disponible='true']
            order by $libro/titulo
            return concat($libro/titulo, ' - ', $libro/autor)
            """;

        return ejecutarXQuery(xquery);
    }

    public static void main(String[] args) {
        try {
            ConsultasBiblioteca cb = new ConsultasBiblioteca();

            System.out.println("=== Buscar 'de' ===");
            cb.buscarPorTitulo("de").forEach(System.out::println);

            System.out.println("\n=== Genero Novela ===");
            cb.buscarPorGenero("Novela").forEach(System.out::println);

            System.out.println("\n=== Estadisticas ===");
            System.out.println(cb.obtenerEstadisticas());

            System.out.println("\n=== Disponibles ===");
            cb.librosDisponibles().forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}