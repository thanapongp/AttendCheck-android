package com.example.tanap.attendcheck.db;

public abstract class Table {
    public static final String TABLE = "";

    public static String getDropSQL() {
        return "DROP TABLE IF EXISTS `" + TABLE + "`;";
    }
}
