package com.ols.ruslan.neo;

/**
 * Перечисление типов
 */
public enum RecordType {
    book,
    inbook,
    proceedings,
    inproceedings,
    article,
    mastersthesis,
    phdthesis,
    techreport,
    misc;

    public static RecordType getType(String type) {
        return RecordType.valueOf(type);
    }

    public boolean notEquals(RecordType type) {
        return !this.equals(type);
    }
}
