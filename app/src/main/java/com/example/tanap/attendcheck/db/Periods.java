package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;

public class Periods extends Table {
    public static final String TABLE = "periods";

    public Periods(Context context) {
        super(context);
    }

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `periods` (\n" +
                "  `" + Column.ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `course_id` TEXT,\n" +
                "  `day` INTEGER,\n" +
                "  `start_time` TEXT,\n" +
                "  `end_time` TEXT,\n" +
                "  `room` TEXT,\n" +
                "  `updated_at` TEXT);";
    }

    public ArrayList<String> getAvailibleDays() {
        Cursor cursor = db.rawQuery("SELECT DISTINCT " +
                "(CASE day " +
                "WHEN 2 THEN 'วันอังคาร' " +
                "WHEN 5 THEN 'วันศุกร์' " +
                "END) AS day FROM periods", null);

        ArrayList<String> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            data.add(cursor.getString(cursor.getColumnIndex("day")));
        }

        cursor.close();

        return data;
    }

    public class Column implements BaseColumns {
        public static final String ID         = BaseColumns._ID;
        public static final String COURSE_ID  = "course_id";
        public static final String DAY        = "day";
        public static final String START_TIME = "start_time";
        public static final String END_TIME   = "end_time";
        public static final String ROOM       = "room";
        public static final String UPDATED_AT = "updated_at";
    }
}
