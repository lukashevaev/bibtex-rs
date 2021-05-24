package com.ols.ruslan.neo;

import java.util.*;
import java.util.stream.Collectors;

public class BibTexBuilder {
    private RecordType recordType;
    private final BibtexInstance instance;

    public BibTexBuilder(final Map<String, String> fields) {
        instance = new BibtexInstance(fields);
        TypeDefiner typeDefiner = new TypeDefiner(instance);
        this.recordType = typeDefiner.defineType();
        refactorFields();
    }

    // Метод для выделения цифр из поля
    private String getDigits(String field) {
        return field.replaceAll("[^0-9-]", "");
    }

    // Имя библиографичксой записи формата Bibtex
    // (указывается до перечисления полей, записываем в форме AuthorYear)
    private String getBibtexKey() {

        String author = instance.getAuthor().isPresent() ?
                Transliterator.cyr2lat(instance.getAuthor().get().split(" ")[0].replaceAll("[^a-zA-Zа-яА-Я]", "")) :
                "Undefined";

        String year = instance.getYear().orElse("Unknown");

        String title = instance.getTitle();

        String firstTitle;

        if (title != null) {
            String firstWord = getFirstWordOfString(title);
            if (PatternFactory.notEmptyFieldPattern.matcher(firstWord).find()) {
                firstTitle = Transliterator.cyr2lat(firstWord);
            } else {
                firstTitle = "";
            }
        } else {
            firstTitle = "";
        }

        return String.format("%s%s%s",
                author, year, firstTitle);
    }

    private String getFirstWordOfString(String source) {
        if (source.contains(" ")) {
            return source.split(" ")[0];
        } else {
            return source;
        }
    }

    // Изменение полей
    private void refactorFields() {

        instance.setEditor(instance.getEditor().replaceAll(",", " and "));

        this.recordType = this.recordType != null ? recordType : RecordType.misc;
        // Удаляем поля
        // RecordType записывается отдельно в самом начале
        // Techreport: если есть это поле- однозначно определяется тип, если его нет- удаляется
        instance.deleteRecordType();
        instance.deleteTechreport();

        instance.getVolume().ifPresent(volume -> instance.setVolume(getDigits(volume)));
        instance.getPages().ifPresent(pages -> instance.setPages(getDigits(pages)));
        instance.getNumber().ifPresent(number -> instance.setNumber(getDigits(number)));
        instance.getYear().ifPresent(year -> instance.setYear(getDigits(year)));

        instance.getAuthor().ifPresent(author -> {
            String[] allAuthors = author.split("_");
            StringBuilder builder = new StringBuilder();
            Arrays.stream(allAuthors).forEach(fullName -> {
                String[] authors = fullName.trim().split(" ");
                String name = authors[0] + ", ";
                builder.append(name);
                Arrays.stream(authors).skip(1).forEach(str -> builder.append(str).append(" "));
                builder.append(" and ");
            });
            if (allAuthors.length == 1) {
                instance.setAuthor(builder.toString().replaceAll("and", ""));
            } else {
                builder.delete(builder.lastIndexOf("and") - 3, builder.length());
                instance.setAuthor(builder.toString().trim());
            }
        });

        // Заменяем "rus" на "russian" (по правилам данного формата)
        if (instance.getLanguage().equals("rus"))
            instance.setLanguage("russian");
        if (instance.getLanguage().equals("eng"))
            instance.setLanguage("english");
        // Удаляем поле том, если оно не удовлетворяет паттерну
        if (!PatternFactory.volumePattern.matcher(instance.getVolume().orElse("").toLowerCase()).find()) {
            instance.deleteVolume();
        }
        // Если не статья, то удаляем номер
        if (recordType.notEquals(RecordType.article)) {
            instance.deleteNumber();
        }
        // Если тип записи статья,  но номер журнала не подходит под паттерн-
        // удаляем его. В противном случае удаляем номер тома
        if (recordType.notEquals(RecordType.article)) {
            if (!PatternFactory.numberPattern.matcher(instance.getNumber().orElse("").toLowerCase()).find()) {
                instance.deleteNumber();
            } else {
                instance.getVolume().ifPresent(instance::setNumber);
                instance.deleteVolume();
            }
        }
       /* String pages = instance.getPages().orElse("");
        if (recordType.notEquals(RecordType.book) & PatternFactory.pagePattern.matcher(pages).find()) {
            instance.deletePages();
        }*/

        //Удаляем пустые поля
        instance.setFields(
                instance.getFields()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != null && !entry.getValue().equals("") && PatternFactory.notEmptyFieldPattern.matcher(entry.getValue()).find())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new)));
    }

    public String buildBibtex() {
        StringBuilder bibTexText = new StringBuilder();
        bibTexText.append(String.format("@%s{%s,\n", recordType.toString(), getBibtexKey()));
        instance.getFields()
                .forEach((key, value) -> bibTexText.append(
                        String.format("  %s={%s},\n",
                                key,
                                value.trim()
                        )));
        // Удаляем "," с последней строки  и добавляем закрывающую "}"
        return bibTexText.substring(0, bibTexText.length() - 2).replaceAll(",\\s*,", ",") + "\n" + '}';
    }
}
