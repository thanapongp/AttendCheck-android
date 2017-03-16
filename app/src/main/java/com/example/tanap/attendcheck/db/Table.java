package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class Table {
    public static final String TABLE = "";

    public Context context;
    public SQLiteDatabase db;

    public Table(Context context) {
        db = new DB(context).getWritableDatabase();
    }

    public void closeDB() {
        db.close();
    }

    public static String getDropSQL() {
        return "DROP TABLE IF EXISTS `" + TABLE + "`;";
    }
}
