package com.example.tanap.attendcheck.db;

import android.provider.BaseColumns;

public class Schedules extends Table {
    public static final String TABLE = "schedules";

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
