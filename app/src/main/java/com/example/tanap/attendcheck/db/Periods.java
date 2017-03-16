package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.provider.BaseColumns;

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
