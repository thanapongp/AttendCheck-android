package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class Table {

    public Context context;
    public SQLiteDatabase db;

    public Table(Context context) {
        db = new DB(context).getWritableDatabase();
    }

    public void closeDB() {
        db.close();
    }

    public void truncate() {
        db.delete(tableName(), null, null);
        db.close();
    }

    public abstract String tableName();
}
