package com.ols.ruslan.neo;


import org.jsoup.helper.StringUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BibTexBuilder{
    private String recordType;
    private final BibtexInstance instance;

    public BibTexBuilder(final Map<String, String> fields) {
        instance = new BibtexInstance(fields);
        TypeDefiner typeDefiner = new TypeDefiner(instance);
        this.recordType = typeDefiner.defineType();
        refactorFields();
    }
    // Имя библиографичксой записи формата Bibtex
    // (указывается до перечисления полей, записываем в форме AuthorYear)
    private String getBibtexKey() {
        if (!instance.getFields().isEmpty())
            return instance.getAuthor().split(" ")[0] + instance.getYear();
        else return "EmptyRecord";
    }
    // Изменение полей
    private void refactorFields(){
        this.recordType = this.recordType != null ? recordType : "misc";
        // Удаляем поля
        // RecordType записывается отдельно в самом начале
        // Techreport: если есть это поле- однозначно определяется тип, если его нет- удаляется
        instance.deleteRecordType();
        instance.deleteTechreport();

        // Удаление "and" в конце поля "author"
        String author = instance.getAuthor();
        if (!StringUtil.isBlank(author)) instance.setAuthor(author.substring(0, author.length() - 4));

        // Заменяем "rus" на "russian" (по правилам данного формата)
        if (instance.getLanguage().equals("rus"))
            instance.setLanguage("russian");
        // Удаляем поле том, если оно не удовлетворяет паттерну
        if (!PatternFactory.volumePattern.matcher(instance.getVolume().toLowerCase()).find()) instance.deleteVolume();
        // Если не статья, то удаляем номер
        if (!"article".equals(recordType)) instance.deleteNumber();
        // Если тип записи статья,  но номер журнала не подходит под паттерн-
        // удаляем его. В противном случае удаляем номер тома
        if ("article".equals(recordType)) {
            if (!PatternFactory.numberPattern.matcher(instance.getNumber().toLowerCase()).find()) instance.deleteNumber();
            else instance.deleteVolume();
        }
        String pages = instance.getPages();
        if (!"book".equals(recordType) & PatternFactory.pagePattern.matcher(pages).find()) instance.deletePages();

        //Удаляем пустые поля
        instance.setFields(
                instance.getFields()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != null && !entry.getValue().equals("") && PatternFactory.notEmptyFieldPattern.matcher(entry.getValue()).find())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue , (a, b) -> a, LinkedHashMap::new)));
    }

    public String buildBibtex() {
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
        // Удаляем "," с последней строки  и добавляем закрывающую "}"
        return bibTexText.substring(0, bibTexText.length() - 2) + "\n" + '}';
    }

}
