package com.example.tanap.attendcheck.db;

import android.provider.BaseColumns;

public class Attendances extends Table {
    public static final String TABLE = "attendances";

    public String getCreateSQL() {
        return "CREATE TABLE `attendances` (\n" +
                "  `" + Column.ID + "` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `schedule_id` int(10) unsigned NOT NULL,\n" +
                "  `student_id` int(10) unsigned NOT NULL,\n" +
                "  `in_time` datetime NOT NULL,\n" +
                "  `late` tinyint(1) NOT NULL DEFAULT '0',\n" +
                "  PRIMARY KEY (`id`));";
    }

    public class Column implements BaseColumns {
        public static final String ID           = BaseColumns._ID;
        public static final String SCHEDULE_ID  = "schedule_id";
        public static final String STUDENT_ID   = "student_id";
        public static final String IN_TIME      = "in_time";
        public static final String LATE         = "late";
    }
}
