package com.ols.record;


import com.ols.ruslan.neo.BibTexBuilder;
import com.ols.ruslan.neo.XmlToBibtexTransformer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        XmlToBibtexTransformer toBibtexTransformer = new XmlToBibtexTransformer();
        toBibtexTransformer.startup();
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("RUSMARC.xml");
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        Document document = null;
        if (inputStream != null) document = docBuilder.parse(inputStream);
        byte[] bytes = getBytes(document);
        System.out.println(Arrays.toString(toBibtexTransformer.transform(bytes, "UTF-8")));
    }

    public static byte[] getBytes(Document document) throws Exception {
        Source source = new DOMSource( document );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = new StreamResult(out);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);
        return out.toByteArray();
    }
}
