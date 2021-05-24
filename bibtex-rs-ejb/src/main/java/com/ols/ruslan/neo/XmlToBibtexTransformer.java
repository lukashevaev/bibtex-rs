package com.ols.ruslan.neo;


import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;


@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.CONTAINER)
@Singleton(name = "XmlToBibtexTransformer")
@Startup
@Remote(MediaTypeTransformerFacade.class)
@EJB(name = "java:global/ruslan/mediaType/application/xml/application/x-bibtex", beanInterface = MediaTypeTransformerFacade.class)
public class XmlToBibtexTransformer implements MediaTypeTransformerFacade {
    private static final Logger log = Logger.getLogger(XmlToBibtexTransformer.class
            .getName());
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private Transformer transformer;
    private DocumentBuilder builder;

    @PostConstruct
    void startup() {
        log.info("Startup");
        try {
            Templates templates = transformerFactory.newTemplates(new StreamSource(
                    XmlToBibtexTransformer.class.getClassLoader().getResourceAsStream(
                            "RUSMARC2BibTex.xsl")));

            // Создаем трансформер для преобразования одного xml в другой
            transformer = templates.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            builder = factory.newDocumentBuilder();

        } catch (TransformerConfigurationException | ParserConfigurationException e) {
            log.severe("Unable to initialise templates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(byte[] content, String encoding) throws Exception {
        DOMResult result = new DOMResult();

        // Создаем источник для преобразования из поступившего массива байт
        Document document = builder.parse(new ByteArrayInputStream(content));

        //Трансформация,парсинг и создание нового формата
        transformer.transform(new DOMSource(document), result);

        Map<String, String> fields = XmlParser.parse((Document) result.getNode());

        BibTexBuilder bibTexBuilder = new BibTexBuilder(fields);

        return bibTexBuilder.buildBibtex().getBytes(encoding);
    }

    public String transformTest(byte[] content) throws Exception {
        DOMResult result = new DOMResult();

        // Создаем источник для преобразования из поступившего массива байт
        Document document = builder.parse(new ByteArrayInputStream(content));

        //Трансформация,парсинг и создание нового формата
        transformer.transform(new DOMSource(document), result);

        Map<String, String> fields = XmlParser.parse((Document) result.getNode());

        BibTexBuilder bibTexBuilder = new BibTexBuilder(fields);

        String bibtex = bibTexBuilder.buildBibtex();

        fillBibTexFile(bibtex);

        return bibtex;
    }

    private void fillBibTexFile(String bibtex) {
        try (FileWriter writer = new FileWriter("src/main/resources/bibtex.txt", false)) {
            writer.write(bibtex);

            writer.flush();
        } catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

}
