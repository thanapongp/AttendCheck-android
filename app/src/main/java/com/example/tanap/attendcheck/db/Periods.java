package com.example.tanap.attendcheck.db;

import android.provider.BaseColumns;

public class Periods extends Table {
    public static final String TABLE = "periods";

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `periods` (\n" +
                "  `" + Column.ID + "` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `course_id` int(10) unsigned NOT NULL,\n" +
                "  `day` tinyint(3) unsigned NOT NULL,\n" +
                "  `start_time` time NOT NULL,\n" +
                "  `end_time` time NOT NULL,\n" +
                "  `room` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',\n" +
                "  `created_at` timestamp NULL DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`));";
    }

    public class Column implements BaseColumns {
        public static final String ID         = BaseColumns._ID;
        public static final String COURSE_ID  = "course_id";
        public static final String day        = "day";
        public static final String START_TIME = "start_time";
        public static final String END_TIME   = "end_time";
        public static final String UPDATED_AT = "updated_at";
    }
}
