package com.ols.ruslan.neo;

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
