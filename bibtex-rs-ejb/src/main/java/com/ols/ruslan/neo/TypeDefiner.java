package com.ols.ruslan.neo;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Данный класс используется для того, чтобы определить тип записи на основании паттернов
 * и на основании обязательных полей
 */
public class TypeDefiner {
    private final Map<RecordType, Set<String>> requiredFields = new HashMap<>();
    private final Map<RecordType, Set<String>> rejectedFields = new HashMap<>();
    private final BibtexInstance instance;

    public TypeDefiner(BibtexInstance instance){
        this.instance = instance;
    }
    // Метод, который определяет тип
    public RecordType defineType(){
        SortedSet<RecordType> recordTypes = new TreeSet<>();
        Map<RecordType, Pattern> typePatterns = new PatternFactory().getTypePatterns();
        String foundRecordType = instance.getRecordType().orElse("");
        fillRequiredFields();
        fillRejectedFields();


        //Поиск типа по паттернам
        for (Map.Entry<RecordType,Pattern> entry : typePatterns.entrySet()) {
                if (entry.getValue().matcher(foundRecordType).find() ||
                        entry.getValue().matcher(instance.getTitle().toLowerCase()).find()) {
                    return defineWithSpecialCases(entry.getKey());
                }
        }

        //Проверка на наличие у записи всех обязательных полей для какого-либо типа и проверка на отсутсвие запрещенных полей этого типа
        //При удачной проверке тип запишется в recordTypes
        requiredFields.forEach((key, value) -> {
            if (instance.getFields().keySet().containsAll(value)
                    && instance.getFields().keySet().stream().noneMatch(field -> rejectedFields.get(key) != null && rejectedFields.get(key).contains(field))) {
                recordTypes.add(key);
            }
        });

        // Если тип не нашелся по паттернам, то сразу выводится выше.
        // Если по обязательным полям найдется один тип, то он выведется. А если больше одного, то поиск продолжается по особым случаям записи
        if (recordTypes.size() == 1){
            return recordTypes.iterator().next();
        } else {
            // Проверка @book: есть общее количество страниц, не 12-25
            String pages = instance.getPages().orElse("");
            if (PatternFactory.pagePattern.matcher(pages).find()
                    && !PatternFactory.pagesPattern.matcher(pages).find()) {
                return RecordType.book;
            }
            if (recordTypes.size() > 1) {
                return recordTypes.first();
            }
        }
        return  RecordType.misc;
    }

    private RecordType defineWithSpecialCases(RecordType type) {
        String pages = instance.getPages().orElse("");
        // Если удовлетворяет паттерну "digits-digits" и подходит под @book, то это @inbook
        if (type.equals(RecordType.book)
                && PatternFactory.pagesPattern.matcher(pages).find()) {
            return RecordType.inbook;
        }
        //Если удовлетворяет паттерну "digits-digits" и подходит под @proceedings, то это @inproceedings
        if (type.equals(RecordType.proceedings)
                && PatternFactory.pagesPattern.matcher(instance.getPages().orElse("")).find()) {
            return RecordType.inproceedings;
        }
        return type;
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
    }

    //Запрещенные поля для каждого типа
    private void fillRejectedFields() {
        rejectedFields.put(RecordType.proceedings, new HashSet<>(Arrays.asList(
                "author"
        )));
        rejectedFields.put(RecordType.book, new HashSet<>(Arrays.asList(
                "journal"
        )));
    }
}
