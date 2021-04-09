package com.ols.record;


import com.ols.z3950.record.Record;
import com.sun.istack.internal.NotNull;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;


@Singleton(name = "bibtex")
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
@Remote(BeanSchema.class)
@EJB(name = "java:global/ruslan/recordSchema/bibtex", beanInterface = BeanSchema.class , beanName = "bibtex")
public class BibtexRecordSchema  implements BeanSchema {
    private static final Logger log = Logger.getLogger(BibtexRecordSchema.class
            .getName());
    private static final TransformerFactory transformerFactory = TransformerFactory
            .newInstance();
    private static Templates templates;

    @EJB(lookup = "java:global/ruslan/recordSchema/ruslan", beanInterface = RecordSchema.class)
    private RecordSchema ruslanRecordSchema;

    @PostConstruct
    public void init() {
        log.fine("Preparing XSL templates");
        log.fine(Objects.requireNonNull(getClass().getClassLoader().getResource("RUSMARC2Bibtex.xsl")).toString());
        try {
            templates = transformerFactory.newTemplates(new StreamSource(
                    getClass().getClassLoader().getResourceAsStream(
                            "RUSMARC2Bibtex.xsl")));

        } catch (TransformerConfigurationException e) {
            log.severe("Unable to initialise templates: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    @NotNull
    public String getTransformedRecord(Record record, String encoding) throws Exception {
        Document src = ruslanRecordSchema.toDocument(record, encoding);
        BibTexBuilder builder = getBuilder(src);
        return builder.buildBibtex();

    }

    @Override
    public String getMimeType() {
        return "application/x-bibtex";
    }

    private BibTexBuilder getBuilder(Document src) throws TransformerException {
        Transformer transformer = templates.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        DOMResult result = new DOMResult();
        transformer.transform(new DOMSource(src), result);
        //получаем поля из схемы для составления формата
        Map<String, String> fields = XmlParser.parse((Document) result.getNode());
        return new BibTexBuilder(fields);
    }

    @Override
    public String toString(Record record, String encoding) throws Exception {
        return ruslanRecordSchema.toString(record, encoding);
    }

    @Override
    public Record normalize(Record record, String encoding) {
        return ruslanRecordSchema.normalize(record, encoding);
    }

    @Override
    public Record denormalize(Record record, String encoding) {
        return ruslanRecordSchema.denormalize(record, encoding);
    }
}