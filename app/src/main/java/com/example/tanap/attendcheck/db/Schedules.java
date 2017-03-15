package com.example.tanap.attendcheck.db;

import android.provider.BaseColumns;

public class Schedules extends Table {
    public static final String TABLE = "schedules";

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `schedules` (\n" +
                "  `" + Column.ID + "` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `course_id` int(10) unsigned NOT NULL,\n" +
                "  `room` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                "  `start_date` datetime NOT NULL,\n" +
                "  `end_date` datetime NOT NULL,\n" +
                "  `updated_at` timestamp NULL DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`));";
    }

    public class Column implements BaseColumns {
        public static final String ID         = BaseColumns._ID;
        public static final String COURSE_ID  = "course_id";
        public static final String ROOMs       = "room";
        public static final String START_DATE = "start_date";
        public static final String END_DATE   = "end_date";
        public static final String UPDATED_AT = "updated_at";
    }
}
