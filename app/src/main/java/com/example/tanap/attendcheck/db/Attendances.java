package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.provider.BaseColumns;

public class Attendances extends Table {
    public static final String TABLE = "attendances";

    public Attendances(Context context) {
        super(context);
    }

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `attendances` (\n" +
                "  `" + Column.ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `schedule_id` INTEGER,\n" +
                "  `student_id` INTEGER,\n" +
                "  `in_time` TEXT,\n" +
                "  `late` INTEGER DEFAULT 0);";
    }

    public class Column implements BaseColumns {
        public static final String ID           = BaseColumns._ID;
        public static final String SCHEDULE_ID  = "schedule_id";
        public static final String STUDENT_ID   = "student_id";
        public static final String IN_TIME      = "in_time";
        public static final String LATE         = "late";
    }
}
