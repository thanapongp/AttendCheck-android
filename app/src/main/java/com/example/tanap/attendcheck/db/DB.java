package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "attendcheck.db";
    Context context;

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(User.getCreateSQL());
        db.execSQL(Courses.getCreateSQL());
        db.execSQL(Schedules.getCreateSQL());
        db.execSQL(Periods.getCreateSQL());
        db.execSQL(Attendances.getCreateSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB Upgrade", String.format("From %s to %s", oldVersion, newVersion));

        db.execSQL(User.getDropSQL());
        db.execSQL(Courses.getDropSQL());
        db.execSQL(Schedules.getDropSQL());
        db.execSQL(Periods.getDropSQL());
        db.execSQL(Attendances.getDropSQL());

        onCreate(db);
    }
}
