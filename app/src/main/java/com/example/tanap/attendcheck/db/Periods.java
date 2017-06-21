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

    @Override
    public String tableName() {
        return "periods";
    }

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `periods` (\n" +
                "  `" + Column.ID + "` INTEGER,\n" +
                "  `course_id` TEXT,\n" +
                "  `day` INTEGER,\n" +
                "  `start_time` TEXT,\n" +
                "  `end_time` TEXT,\n" +
                "  `room` TEXT,\n" +
                "  `updated_at` TEXT);";
    }

    public static String getDropSQL() {
        return "DROP TABLE IF EXISTS `" + TABLE + "`;";
    }

    public ArrayList<String> getAvailableDays() {
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

        super.closeDB();

        return data;
    }

    public ArrayList<HashMap<String, String>> getPeriodsOnSelectedDay(Integer dayValue) {
        Cursor cursor = db.rawQuery(
                "SELECT courses.code, courses.section, courses.semester, courses.year, courses.name, " +
                        "periods.room, periods.start_time, periods.end_time " +
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

            String courseName = cursor.getString(cursor.getColumnIndex("code")) + " "
                              + cursor.getString(cursor.getColumnIndex("name"));

            String year = cursor.getString(cursor.getColumnIndex("year"));

            String courseCode = cursor.getString(cursor.getColumnIndex("code"))    + "-"
                              + semesterValue(cursor.getString(cursor.getColumnIndex("semester"))) + "-"
                              + year.substring(year.length() - 2) + "-"
                              + cursor.getString(cursor.getColumnIndex("section"));

            dataHashMap.put("code", courseCode);
            dataHashMap.put("name", courseName);
            dataHashMap.put("room", cursor.getString(cursor.getColumnIndex("room")));
            dataHashMap.put("start_time", start_time + " - " + end_time);
            dataHashMap.put("end_time", cursor.getString(cursor.getColumnIndex("end_time")));

            data.add(dataHashMap);
        }

        cursor.close();

        super.closeDB();

        return data;
    }

    private String semesterValue(String semester)
    {
        switch (semester) {
            case "ภาคต้น":
                return "1";
            case "ภาคปลาย":
                return "2";
            case "ภาคฤดูร้อน":
                return "3";
            default:
                return "null";
        }
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
