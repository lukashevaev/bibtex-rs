package com.ols.ruslan.neo;


import org.jsoup.helper.StringUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BibTexBuilder{
    private String recordType;
    BibtexInstance instance;

    public BibTexBuilder(final Map<String, String> fields) {
        instance = new BibtexInstance(fields);
        TypeDefiner typeDefiner = new TypeDefiner(instance);
        this.recordType = typeDefiner.getRecordType();
        refactorFields();
    }

    private String getBibtexKey() {
        if (!instance.getFields().isEmpty())
            return instance.getAuthor().split(" ")[0] + instance.getYear();
        else return "EmptyRecord";
    }

    private void refactorFields(){
        this.recordType = this.recordType != null ? recordType : "Undefined";
        //these fields are not needed in bibtex(recordType will be on the top of bibtex-record, techreport is only a flag)
        instance.deleteRecordType();
        instance.deleteTechreport();

        //deleting "and" in the end of field "author"
            String author = instance.getAuthor();
            if (!StringUtil.isBlank(author)) instance.setAuthor(author.substring(0, author.length() - 4));

        //change "rus" to "russian" if exists
            if (instance.getLanguage().equals("rus"))
                instance.setLanguage("russian");
        //check that volume matches a specific pattern (if exists)
            if (!PatternFactory.volumePattern.matcher(instance.getVolume().toLowerCase()).find()) instance.deleteVolume();
        //check that number of journal matches a specific pattern (if exists)

            if (!"@article".equals(recordType)) instance.setNumber("");


        if ("@article".equals(recordType)) {
            if (!PatternFactory.numberPattern.matcher(instance.getNumber().toLowerCase()).find()) instance.deleteNumber();
            else instance.deleteVolume();
        }
        String pages = instance.getPages();
        if (!"@book".equals(recordType) & PatternFactory.pagePattern.matcher(pages).find()) instance.deletePages();

    }

    public String buildBibtex() {
        instance.setFields(
                instance.getFields()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != null && !entry.getValue().equals(""))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue , (a, b) -> a, LinkedHashMap::new)));
        StringBuilder bibTexText = new StringBuilder();
        bibTexText.append("@")
                    .append(recordType)
                    .append("{")
                    .append(getBibtexKey())
                    .append(",\n");
        instance.getFields().forEach((key, value) -> bibTexText.append("  ")
                .append(key)
                .append("={")
                .append(value.trim())
                .append("},\n"));
        //deleting "," from last body-line and adding a closing "}"
        return bibTexText.substring(0, bibTexText.length() - 2) + "\n" + '}';
    }

}
