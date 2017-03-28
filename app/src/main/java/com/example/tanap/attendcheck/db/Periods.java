package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

    public ArrayList<String> getAvailibleDays() {
        Cursor cursor = db.rawQuery("SELECT DISTINCT " +
                "(CASE day " +
                "WHEN 1 THEN 'วันจันทร์' " +
                "WHEN 2 THEN 'วันอังคาร' " +
                "WHEN 3 THEN 'วันพุธ' " +
                "WHEN 4 THEN 'วันพฤหัส' " +
                "WHEN 5 THEN 'วันศุกร์' " +
                "WHEN 6 THEN 'วันเสาร์'" +
                "WHEN 7 THEN 'วันอาทิตย์'" +
                "END) AS day FROM periods", null);

        ArrayList<String> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            data.add(cursor.getString(cursor.getColumnIndex("day")));
        }

        cursor.close();

        return data;
    }

    public ArrayList<HashMap<String, String>> getPeriodsOnSelectedDay(Integer dayValue) {
        Cursor cursor = db.rawQuery(
                "SELECT courses.name, periods.room, periods.start_time, periods.end_time " +
                "FROM courses, periods " +
                "WHERE periods.day = ? AND periods.course_id = courses._id",
                new String[] { dayValue.toString() });

        ArrayList<HashMap<String, String>> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            HashMap<String, String> dataHashMap = new HashMap<>();

            SimpleDateFormat inFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            String start_time = "";
            String end_time = "";

            try {
                Date start_date = inFormat.parse(cursor.getString(cursor.getColumnIndex("start_time")));
                Date end_date = inFormat.parse(cursor.getString(cursor.getColumnIndex("end_time")));

                start_time = sdf.format(start_date);
                end_time = sdf.format(end_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            dataHashMap.put("name", cursor.getString(cursor.getColumnIndex("name")));
            dataHashMap.put("room", cursor.getString(cursor.getColumnIndex("room")));
            dataHashMap.put("start_time", start_time + " - " + end_time);
            dataHashMap.put("end_time", cursor.getString(cursor.getColumnIndex("end_time")));

            data.add(dataHashMap);
        }

        cursor.close();

        return data;
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
