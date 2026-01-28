package org.example;

import javax.xml.validation.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;

public class ValidadorXML {
    
    private Schema schema;
    
    public ValidadorXML(String xsdPath) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(
            "http://www.w3.org/2001/XMLSchema");
        schema = factory.newSchema(new File(xsdPath));
    }
    
    public boolean validar(String xml) {
        try {
            Validator validator = schema.newValidator();
            validator.validate(
                new StreamSource(new StringReader(xml)));
            return true;
        } catch (Exception e) {
            System.err.println("Validacion fallida: " + e.getMessage());
            return false;
        }
    }
    
    public void guardarSiValido(String colPath, String nombre, 
            String xml) throws Exception {
        
        if (validar(xml)) {
            AlmacenDocumentos.guardarDesdeString(colPath, nombre, xml);
        } else {
            throw new Exception("Documento no valido");
        }
    }
}