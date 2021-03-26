package com.ols.record;

public enum RecordType {
    book,
    mvbook,
    proceedings,
    article,
    mastersthesis,
    phdthesis,
    techreport;

    public static RecordType getType(String type) {
        return RecordType.valueOf(type);
    }
}
