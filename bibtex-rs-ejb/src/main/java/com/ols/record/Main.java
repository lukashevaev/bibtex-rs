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
        BibtexRecordSchema recordSchema = new BibtexRecordSchema();
        recordSchema.init();
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("RUSMARC.xml");
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        Document document = null;
        if (inputStream != null) document = docBuilder.parse(inputStream);
        //BibTexBuilder builder = recordSchema.getBuilder(document);
        //System.out.println(builder.buildBibtex());
    }
}
