package com.ols.ruslan.neo;


import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
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
    private static Templates templates;


    @PostConstruct
    private void startup() {
        log.info("Startup");
        try {
            templates = transformerFactory.newTemplates(new StreamSource(
                    XmlToBibtexTransformer.class.getClassLoader().getResourceAsStream(
                            "RUSMARC2BibTex.xsl")));

        } catch (TransformerConfigurationException e) {
            log.severe("Unable to initialise templates: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public byte[] transform(byte[] content, String encoding) throws Exception {
        // Создаем трансформер для преобразования одного xml в другой
        Transformer transformer = templates.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        DOMResult result = new DOMResult();

        // Создаем источник для преобразования из поступившего массива байт
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(content));

        //Трансформация,парсинг и создание нового формата
        transformer.transform(new DOMSource(document), result);
        Map<String, String> fields = XmlParser.parse((Document) result.getNode());
        BibTexBuilder bibTexBuilder = new BibTexBuilder(fields);

        return bibTexBuilder.buildBibtex().getBytes(encoding);
    }

}
