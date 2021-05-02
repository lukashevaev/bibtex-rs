package com.ols.ruslan.neo;

import java.util.*;
import java.util.regex.Pattern;

public class TypeDefiner {
    private final Map<String, String> fields;
    private String recordType;
    private final Map<RecordType, Pattern> patternsForType;
    private final Map<RecordType, Set<String>> requiredFields = new HashMap<>();
    private final Map<RecordType, Set<String>> rejectedFields = new HashMap<>();
    private Set<String> recordTypes = new HashSet<>();

    public TypeDefiner(final Map<String, String> fields){
        PatternFactory patternFactory = PatternFactory.getInstance();
        patternsForType = patternFactory.getPatternsForType();
        this.fields = fields;
        if (!fields.isEmpty()) {
            recordType = this.fields.get("recordType").toLowerCase();
            fillRequiredFields();
            fillRejectedFields();
            defineType();
        }
    }
    // Метод, который определяет тип
    private void defineType(){
        boolean isChanged = false;
        String currentFoundRecordType = null;
        //Поиск типа по паттернам
        for (Map.Entry<RecordType,Pattern> entry : patternsForType.entrySet()) {
                if (entry.getValue().matcher(recordType).find() ||
                        entry.getValue().matcher(fields.get("title").toLowerCase()).find()) {
                    currentFoundRecordType = entry.getKey().toString();
                    recordType = currentFoundRecordType;
                    return;
                }
        }


        //Проверка на наличие у записи всех обязательных полей для какого-либо типа и проверка на отсутсвие запрещенных полей этого типа
        //При удачной проверке тип запишется в recordTypes
        requiredFields.forEach((key, value) -> {
            if (fields.keySet().containsAll(value)
                    && fields.keySet().stream().noneMatch(field -> rejectedFields.get(key) != null && rejectedFields.get(key).contains(field))) {
                recordTypes.add(key.toString());
            }
        });
        // Если тип не нашелся по паттернам, то поиск по обязательным полям и отсутствию запрещенных
        // Если по обязательным полям найдется один тип, то он выведется. А если больше одного, то поиск продолжается по особым случаям записи
        if (recordTypes.size() == 1){
            recordType = recordTypes.iterator().next();
            return;
        } else {
            //searchForSpecialCases  ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ ДОДЕЛАТЬ
            //checkForTechReport
            if (fields.get("techreport") != null) {
                recordType = "techreport";
                return;
            }

            /*//checkForArticle
            if (fields.get("journal") != null) {
                if (patternsForType.get(RecordType.article).matcher(fields.get("journal").toLowerCase()).find()) {
                    recordType = "article";
                    return;
                }
                if (!recordType.equals("article")) fields.remove("journal");
            }*/

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

    //Обязательные поля для каждого типа
    private void fillRequiredFields() {
        requiredFields.put(RecordType.book, new HashSet<>(Arrays.asList(
                "author",
                "title",
                "year",
                "publisher"
        )));
        requiredFields.put(RecordType.article, new HashSet<>(Arrays.asList(
                "author",
                "title",
                "journal",
                "year"
        )));
        requiredFields.put(RecordType.proceedings, new HashSet<>(Arrays.asList(
                "title",
                "year"
        )));

//        requiredFields.put(RecordType.techreport, new HashSet<>(Arrays.asList(
//                "title",
//                "year"
//        )));
    }

    //Запрещенные поля для каждого типа
    private void fillRejectedFields() {
        rejectedFields.put(RecordType.proceedings, new HashSet<>(Arrays.asList(
                "author"
        )));
        rejectedFields.put(RecordType.book, new HashSet<>(Arrays.asList(
                "journal"
        )));
        rejectedFields.put(RecordType.proceedings, new HashSet<>(Arrays.asList(
                "author"
        )));
    }

    public String getRecordType(){
        return recordType;
    }
}
