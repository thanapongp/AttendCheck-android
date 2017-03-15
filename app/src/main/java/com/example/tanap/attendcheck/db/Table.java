package com.example.tanap.attendcheck.db;

public abstract class Table {
    public static final String TABLE = "";

    public abstract String getCreateSQL();

    public String getDropSQL() {
        return "DROP TABLE IF EXISTS `" + TABLE + "`;";
    }
}
