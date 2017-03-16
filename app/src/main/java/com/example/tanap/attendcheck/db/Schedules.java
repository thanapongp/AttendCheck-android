package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;

public class Schedules extends Table {
    public static final String TABLE = "schedules";

    public Schedules(Context context) {
        super(context);
    }

    public ArrayList<HashMap<String, String>> getNextOrCurrentSchedule() {
        Cursor cursor = db.rawQuery(
                "SELECT courses.code, courses.name, schedules.room, schedules.start_date, schedules.end_date " +
                        "FROM courses, schedules " +
                        "WHERE schedules.course_id = courses._id AND start_date > (DATETIME('now')) " +
                        "ORDER BY schedules.start_date ASC LIMIT 1;", null);

        ArrayList<HashMap<String, String>> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            HashMap<String, String> dataHashMap = new HashMap<>();

            dataHashMap.put("code", cursor.getString(cursor.getColumnIndex("code")));
            dataHashMap.put("name", cursor.getString(cursor.getColumnIndex("name")));
            dataHashMap.put("room", cursor.getString(cursor.getColumnIndex("room")));
            dataHashMap.put("start_date", cursor.getString(cursor.getColumnIndex("start_date")));
            dataHashMap.put("end_date", cursor.getString(cursor.getColumnIndex("start_date")));

            data.add(dataHashMap);
        }

        cursor.close();

        return data;
    }

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `schedules` (\n" +
                "  `" + Column.ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `course_id` TEXT,\n" +
                "  `room` TEXT,\n" +
                "  `start_date` TEXT,\n" +
                "  `end_date` TEXT,\n" +
                "  `updated_at` TEXT);";
    }

    public class Column implements BaseColumns {
        public static final String ID         = BaseColumns._ID;
        public static final String COURSE_ID  = "course_id";
        public static final String ROOM       = "room";
        public static final String START_DATE = "start_date";
        public static final String END_DATE   = "end_date";
        public static final String UPDATED_AT = "updated_at";
    }
}
