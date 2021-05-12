package com.ols.ruslan.neo;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
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
        return field.replaceAll("[^0-9]", "");
    }


    // Имя библиографичксой записи формата Bibtex
    // (указывается до перечисления полей, записываем в форме AuthorYear)
    private String getBibtexKey() {
        return String.format("%s%s",
                !instance.getAuthor().equals("")
                        ? Transliterator.cyr2lat(instance.getAuthor().split(" ")[0].replaceAll("[^a-zA-Zа-яА-Я]", ""))
                        : "Undefined",
                !instance.getYear().equals("")
                        ? instance.getYear()
                        : "Unknown");
    }
    // Изменение полей
    private void refactorFields(){
        this.recordType = this.recordType != null ? recordType : RecordType.misc;
        // Удаляем поля
        // RecordType записывается отдельно в самом начале
        // Techreport: если есть это поле- однозначно определяется тип, если его нет- удаляется
        instance.deleteRecordType();
        instance.deleteTechreport();

        // Удаление "and" в конце поля "author"
        String author = instance.getAuthor();
        String[] allAuthors = author.split("and");
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
            instance.setAuthor(builder.toString().trim().substring(0, builder.length() - 4));
        }


        // Заменяем "rus" на "russian" (по правилам данного формата)
        if (instance.getLanguage().equals("rus"))
            instance.setLanguage("russian");
        if (instance.getLanguage().equals("eng"))
            instance.setLanguage("english");
        // Удаляем поле том, если оно не удовлетворяет паттерну
        if (!PatternFactory.volumePattern.matcher(instance.getVolume().toLowerCase()).find()) instance.deleteVolume();
        // Если не статья, то удаляем номер
        if (recordType.notEquals(RecordType.article)) instance.deleteNumber();
        // Если тип записи статья,  но номер журнала не подходит под паттерн-
        // удаляем его. В противном случае удаляем номер тома
        if (recordType.notEquals(RecordType.article)) {
            if (!PatternFactory.numberPattern.matcher(instance.getNumber().toLowerCase()).find()) instance.deleteNumber();
            else instance.deleteVolume();
        }

        instance.setPages(getDigits(instance.getPages()));
        String pages = instance.getPages();
        if (recordType.notEquals(RecordType.book) & PatternFactory.pagePattern.matcher(pages).find()) instance.deletePages();


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
