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

    @Override
    public String tableName() {
        return "attendances";
    }

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `attendances` (\n" +
                "  `" + Column.ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `schedule_id` INTEGER);";
    }

    public static String getDropSQL() {
        return "DROP TABLE IF EXISTS `" + TABLE + "`;";
    }

    public Boolean checkIfAlreadyAttendance(Integer scheduleID) {
        Cursor cursor = db.rawQuery("SELECT * FROM attendances WHERE schedule_id = ?",
                new String[] { scheduleID.toString() });

        boolean alreadyAttended = (cursor.getCount() != 0);

        cursor.close();

        super.closeDB();

        return alreadyAttended;
    }

    public void attend(Integer scheduleID) {
        ContentValues value = new ContentValues();
        value.put(Column.SCHEDULE_ID, scheduleID);

        db.insert(TABLE, null,value);
        db.close();
    }

    public class Column implements BaseColumns {
        public static final String ID           = BaseColumns._ID;
        public static final String SCHEDULE_ID  = "schedule_id";
    }
}
