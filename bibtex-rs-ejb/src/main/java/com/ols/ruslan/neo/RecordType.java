package com.ols.ruslan.neo;

/**
 * Перечисление типов
 */
public enum RecordType {
    book,
    inbook,
    proceedings,
    article,
    mastersthesis,
    phdthesis,
    techreport,
    misc;

    public static RecordType getType(String type) {
        return RecordType.valueOf(type);
    }
}
