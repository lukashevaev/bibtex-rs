package com.ols.record;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class is used to create patterns to find the required fields and to check them for the correct format.
 */
public class PatternFactory {
    private static final Map<RecordType, Pattern> patternsForType = new HashMap<>();

    private PatternFactory(){
        patternsForType.put(RecordType.book,
                        Pattern.compile("энциклопедия|encyclopaedia"));
        patternsForType.put(RecordType.mvbook,
                        Pattern.compile(
                                "сборник|собрание|сочинения|работы|" +
                                "((в|in)\\s\\d+-?х?\\s(т|ч|vols)\\.?)$"));
        patternsForType.put(RecordType.proceedings,
                        Pattern.compile(
                                "proceedings|" +
                                "of\\s*(a|the)\\s*conference|" +
                                        "conference|proceedings\\s*of|" +
                                        "of\\s*(a|the).*\\s*colloquium|" +
                                        "of\\s*symposia|symposium|" +
                                        "of\\s*(a|the)\\s*congress"));
        patternsForType.put(RecordType.article,
                        Pattern.compile("журнал|journal|статья|article"));
        patternsForType.put(RecordType.mastersthesis,
                        Pattern.compile(
                                "дис.*маг|" +
                                "(master(s)?)?\\s*thesis\\s*((of)?\\smaster)?"));
        patternsForType.put(RecordType.phdthesis,
                        Pattern.compile("дис.*канд"));
        patternsForType.put(RecordType.techreport,
                        Pattern.compile("technical report"));
    }

    private static class PatternFactoryHolder {
        private static final PatternFactory instance = new PatternFactory();
    }

    public static PatternFactory getInstance(){
        return PatternFactoryHolder.instance;
    }
    /** For field "pages"
     * check if field matches pattern "digits-digits"
     * for example "10-20", "345-466"
     */
    public static final Pattern pagesPattern = Pattern.compile("\\D*\\d*-\\d*");
    /**
     * For field "volume"
     * check if field matches pattern like : "chapter 3", "#5", "№ 9", "том 8", "vol № 12"
     * in short it checks that field contains volume or chapter of smth
     */
    public static final Pattern volumePattern = Pattern.compile("^((том|vol|chapter|[nтpч№#]|part|часть)\\.?\\s*[нn№#]?\\s*\\d*)");
    /**
     * For field "number"
     * check if field matches pattern like : "N. 15", "number 8", "№ 9"
     * in short it checks that field is the number of journal
     */
    public static final Pattern numberPattern = Pattern.compile("^(([#№n]|number)\\.?\\s*\\d*)");

    public static final Pattern pagePattern = Pattern.compile("\\d*\\s*(pages|[pсc]|стр|страниц)\\.?");

    public Map<RecordType, Pattern> getPatternsForType() {
        return patternsForType;
    }

}
