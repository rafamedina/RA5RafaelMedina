package org.example.Ejercicio6;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "libro")
@XmlAccessorType(XmlAccessType.FIELD)
public class Libro {
    
    @XmlAttribute
    private String id;
    
    @XmlAttribute
    private String isbn;
    
    @XmlElement
    private String titulo;
    
    @XmlElement
    private String autor;
    
    @XmlElement
    private int anio;
    
    // Esto maneja la estructura <generos><genero>...</genero><genero>...</genero></generos>
    @XmlElementWrapper(name = "generos")
    @XmlElement(name = "genero")
    private List<String> generos;
    
    @XmlElement
    private int paginas;
    
    @XmlElement
    private boolean disponible;
    
    // Constructor vacío OBLIGATORIO para JAXB
    public Libro() {}
    
    public Libro(String id, String titulo, String autor, int anio) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.anio = anio;
        this.disponible = true;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    
    public List<String> getGeneros() { return generos; }
    public void setGeneros(List<String> generos) { this.generos = generos; }
    
    public int getPaginas() { return paginas; }
    public void setPaginas(int paginas) { this.paginas = paginas; }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    @Override
    public String toString() {
        return String.format("Libro[%s] %s - %s (%d)", id, titulo, autor, anio);
    }
}