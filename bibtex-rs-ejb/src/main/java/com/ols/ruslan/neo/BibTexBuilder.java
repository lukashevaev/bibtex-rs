package com.ols.ruslan.neo;

import java.util.Map;

public class BibTexBuilder{
    private String recordType;
    private final Map<String, String> fields;

    public BibTexBuilder(final Map<String, String> fields) {
        this.fields = fields;
        TypeDefiner typeDefiner = new TypeDefiner(fields);
        this.recordType = typeDefiner.getRecordType();
        refactorFields();
    }

    private String getBibtexKey() {
        if (!this.fields.isEmpty())
            return fields.get("author").split(" ")[0] + fields.get("year");
        else return "EmptyRecord";
    }

    private void refactorFields(){
        this.recordType = this.recordType != null ? recordType : "Undefined";
        //these fields are not needed in bibtex(recordType will be on the top of bibtex-record, techreport is only a flag)
        fields.remove("recordType");
        fields.remove("techreport");

        //deleting "and" in the end of field "author"
        if (fields.get("author") != null){
            String author = fields.get("author");
            fields.put("author", author.substring(0, author.length() - 4));
        }

        //change "rus" to "russian" if exists
        if (fields.get("language") != null) {
            if (fields.get("language").equals("rus"))
                fields.put("language", "russian");
        }
        //check that volume matches a specific pattern (if exists)
        if (fields.get("volume") != null) {
            if (!PatternFactory.volumePattern.matcher(fields.get("volume").toLowerCase()).find()) fields.remove("volume");
        }
        //check that number of journal matches a specific pattern (if exists)
        if (fields.get("number") != null){
            if (!"@article".equals(recordType)) fields.remove("number");
        }


        if ("@article".equals(recordType) & fields.get("number") != null) {
            if (!PatternFactory.numberPattern.matcher(fields.get("number").toLowerCase()).find()) fields.remove("number");
            else fields.remove("volume");
        }
        String pages = fields.get("pages") != null ? fields.get("pages").toLowerCase() : "";
        if (!"@book".equals(recordType) & PatternFactory.pagePattern.matcher(pages).find()) fields.remove("pages");

    }

    public String buildBibtex() {
        StringBuilder bibTexText = new StringBuilder();
        bibTexText.append("@")
                    .append(recordType)
                    .append("{")
                    .append(getBibtexKey())
                    .append(",\n");
        fields.forEach((key, value) -> bibTexText.append("  ")
                .append(key)
                .append("={")
                .append(value.trim())
                .append("},\n"));
        //deleting "," from last body-line and adding a closing "}"
        return bibTexText.substring(0, bibTexText.length() - 2) + "\n" + '}';
    }

    /*public org.w3c.dom.Document buildHtml() {
        Document document = Jsoup.parse("<!DOCTYPE html>");
        Document doc = Jsoup.parse("<html></html>");
        StringBuilder bibTexText = new StringBuilder();
        bibTexText.append("@")
                .append(recordType)
                .append("{")
                .append(getBibtexKey())
                .append(",\n");
        fields.forEach((key, value) -> bibTexText.append("  ")
                .append(key)
                .append("={")
                .append(value)
                .append("},\n"));
        //deleting "," from last body-line and adding a closing "}"
        //doc.body().appendText(bibTexText.substring(0, bibTexText.length() - 2) + "\n" + '}');
        //System.out.println(doc.append(document.outerHtml()));
        document.append(doc.outerHtml());
        document.title("bibtex");
        document.body().appendText(bibTexText.substring(0, bibTexText.length() - 2) + "\n" + '}');
        //document.title("bibtex");
        System.out.println(document);
        W3CDom w3cDom = new W3CDom();
        return w3cDom.fromJsoup(doc);
    }*/


}
