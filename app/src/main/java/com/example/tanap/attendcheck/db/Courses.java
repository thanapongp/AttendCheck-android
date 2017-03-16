package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.provider.BaseColumns;

public class Courses extends Table {
    public static final String TABLE = "courses";

    public Courses(Context context) {
        super(context);
    }

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `courses` (\n" +
                "  `" + Column.ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `code` TEXT,\n" +
                "  `name` TEXT,\n" +
                "  `section` INTEGER,\n" +
                "  `semester` TEXT,\n" +
                "  `year` INTEGER,\n" +
                "  `late_time` INTEGER,\n" +
                "  `updated_at` TEXT);";
    }

    public class Column implements BaseColumns {
        public static final String ID         = BaseColumns._ID;
        public static final String CODE       = "code";
        public static final String NAME       = "name";
        public static final String SECTION    = "section";
        public static final String SEMESTER   = "semester";
        public static final String YEAR       = "year";
        public static final String LATE_TIME  = "late_time";
        public static final String UPDATED_AT = "updated_at";
    }
}
