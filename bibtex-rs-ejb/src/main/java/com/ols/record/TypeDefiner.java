package com.ols.record;

import java.util.Map;
import java.util.regex.Pattern;

public class TypeDefiner {
    private final Map<String, String> fields;
    private String recordType;
    private final Map<RecordType, Pattern> patternsForType;

    public TypeDefiner(final Map<String, String> fields){
        PatternFactory patternFactory = PatternFactory.getInstance();
        patternsForType = patternFactory.getPatternsForType();
        this.fields = fields;
        if (!fields.isEmpty()) {
            recordType = this.fields.get("recordType").toLowerCase() ;
            defineType();
        }

        /*recordType = this.fields.get("recordType").toLowerCase() ;
        defineType();*/
    }

    private void defineType(){
        boolean isChanged = false;
        //patternsLookup
        for (Map.Entry<RecordType,Pattern> entry : patternsForType.entrySet()) {
                if (entry.getValue().matcher(recordType).find() ||
                        entry.getValue().matcher(fields.get("title").toLowerCase()).find()) {
                    recordType =  entry.getKey().toString();
                    isChanged = true;
                }
        }
        //searchForSpecialCases
        //checkForTechReport
        if(fields.get("techreport") != null) { 
            recordType = "techreport";
            return;
        }

        //checkForArticle
        if (fields.get("journal") != null) {
                if (patternsForType.get(RecordType.article).matcher(fields.get("journal").toLowerCase()).find()) {
                    recordType = "article";
                    return;
                }
            if (!recordType.equals("article")) fields.remove("journal");
        }
            //check for @book
            String pages = fields.get("pages") != null ? fields.get("pages").toLowerCase() : "";
                if (PatternFactory.pagePattern.matcher(pages).find()
                        & !PatternFactory.pagesPattern.matcher(pages).find()) {
                    recordType = "book";
                    //return;
                }

                if (recordType.equals("book") & PatternFactory.pagesPattern.matcher(pages).find()) recordType = "inbook";

            //checkForProceedings
            if (recordType.equals("proceedings")){
                if (fields.get("pages") != null){
                    if (PatternFactory.pagesPattern.matcher(fields.get("pages")).find()) recordType = "inproceedings";
                }
            }
            if (!isChanged) recordType = "misc";
    }

    public String getRecordType(){
        return recordType;
    }
}
