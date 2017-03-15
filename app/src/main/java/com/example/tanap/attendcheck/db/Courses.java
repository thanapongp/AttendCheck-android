package com.example.tanap.attendcheck.db;

import android.provider.BaseColumns;

public class Courses extends Table {
    public static final String TABLE = "courses";

    public String getCreateSQL() {
        return "CREATE TABLE `courses` (\n" +
                "  `" + Column.ID + "` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `code` varchar(191) NOT NULL,\n" +
                "  `name` varchar(191) NOT NULL,\n" +
                "  `section` int(10) unsigned NOT NULL,\n" +
                "  `semester` varchar(191) NOT NULL,\n" +
                "  `year` int(10) unsigned NOT NULL,\n" +
                "  `late_time` tinyint(3) unsigned NOT NULL,\n" +
                "  `updated_at` timestamp NULL DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`));";
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
