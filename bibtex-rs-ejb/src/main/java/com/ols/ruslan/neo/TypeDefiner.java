package com.ols.ruslan.neo;

import java.util.*;
import java.util.regex.Pattern;

public class TypeDefiner {
    private final Map<String, String> fields;
    private String recordType;
    private final Map<RecordType, Pattern> patternsForType;
    private final Map<RecordType, Set<String>> requiredFields = new HashMap<>();
    private Set<String> recordTypes = new HashSet<>();

    public TypeDefiner(final Map<String, String> fields){
        PatternFactory patternFactory = PatternFactory.getInstance();
        patternsForType = patternFactory.getPatternsForType();
        this.fields = fields;
        if (!fields.isEmpty()) {
            recordType = this.fields.get("recordType").toLowerCase();
            fillRequiredFields();
            defineType();
        }
    }

    private void defineType(){
        boolean isChanged = false;
        String currentFoundRecordType = null;
        //patternsLookup
        for (Map.Entry<RecordType,Pattern> entry : patternsForType.entrySet()) {
                if (entry.getValue().matcher(recordType).find() ||
                        entry.getValue().matcher(fields.get("title").toLowerCase()).find()) {
                    currentFoundRecordType = entry.getKey().toString();
                    recordTypes.add(currentFoundRecordType);
                    isChanged = true;
                }
        }


        requiredFields.forEach((key, value) -> {
            if (fields.keySet().containsAll(value)) {
                recordTypes.add(key.toString());
            }
        });

        if (recordTypes.contains(currentFoundRecordType)) {
            recordType = currentFoundRecordType;
            return;
        } else if (recordTypes.size() == 1){
            recordType = recordTypes.iterator().next();
            return;
        } else {
            //searchForSpecialCases
            //checkForTechReport
            if (fields.get("techreport") != null) {
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
            if (recordType.equals("proceedings")) {
                if (fields.get("pages") != null) {
                    if (PatternFactory.pagesPattern.matcher(fields.get("pages")).find()) recordType = "inproceedings";
                }
            }
        }
            if (!isChanged) recordType = "misc";
    }

    private void fillRequiredFields() {
        requiredFields.put(RecordType.book, new HashSet<>(Arrays.asList(
                "author",
                "title",
                "year",
                "publisher"
        )));
    }

    public String getRecordType(){
        return recordType;
    }
}
