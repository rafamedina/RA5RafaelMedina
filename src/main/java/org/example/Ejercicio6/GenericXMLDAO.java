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

// Interface generica
public interface GenericXMLDAO<T> {
    void guardar(T entity) throws Exception;
    Optional<T> buscarPorId(String id) throws Exception;
    List<T> buscarTodos() throws Exception;
    void actualizar(T entity) throws Exception;
    void eliminar(String id) throws Exception;
}

