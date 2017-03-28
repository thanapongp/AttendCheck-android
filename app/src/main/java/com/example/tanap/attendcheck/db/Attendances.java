package com.example.tanap.attendcheck.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    public Boolean checkIfAlreadyAttendance(Integer scheduleID) {
        Cursor cursor = db.rawQuery("SELECT * FROM attendances WHERE schedule_id = ?",
                new String[] { scheduleID.toString() });

        return cursor.getCount() != 0;
    }

    public void attend(Integer scheduleID, String in_time) {
        ContentValues value = new ContentValues();
        value.put(Column.SCHEDULE_ID, scheduleID);
        value.put(Column.STUDENT_ID, 1);
        value.put(Column.IN_TIME, in_time);
        value.put(Column.LATE, 0);

        db.insert(TABLE, null,value);
        db.close();
    }

    public class Column implements BaseColumns {
        public static final String ID           = BaseColumns._ID;
        public static final String SCHEDULE_ID  = "schedule_id";
        public static final String STUDENT_ID   = "student_id";
        public static final String IN_TIME      = "in_time";
        public static final String LATE         = "late";
    }
}
