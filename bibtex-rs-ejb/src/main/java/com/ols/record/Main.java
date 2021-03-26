package com.ols.record;


import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws TransformerException, IOException, ParserConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NamingException, SAXException {
        //preparing rusmarc.xml for reading
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("RUSMARC.xml");
        //creating document for transformed(by xsl) rusmarc.xml
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        Document document = null;
        if (inputStream != null) document = docBuilder.parse(inputStream);
        BibtexRecordSchema bibtexRecordSchema = new BibtexRecordSchema();
        BibtexRecordSchema.class.getDeclaredMethod("init", new Class[]{}).invoke(bibtexRecordSchema);
        System.out.println(bibtexRecordSchema.transformSchemaToText(document));
    }
}
