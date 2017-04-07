package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;

public class User extends Table {
    public static final String TABLE = "users";

    public User(Context context) {
        super(context);
    }

    @Override
    public String tableName() {
        return "users";
    }

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `users` (\n" +
                "  `" + Column.ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `username` TEXT,\n" +
                "  `password` TEXT,\n" +
                "  `email` TEXT,\n" +
                "  `title` TEXT,\n" +
                "  `name` TEXT,\n" +
                "  `lastname` TEXT,\n" +
                "  `uid` TEXT,\n" +
                "  `updated_at` TEXT);";
    }

    public static String getDropSQL() {
        return "DROP TABLE IF EXISTS `" + TABLE + "`;";
    }

    public ArrayList<HashMap<String, String>> getUserInfo() {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users", null);

        ArrayList<HashMap<String, String>> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            HashMap<String, String> dataHashMap = new HashMap<>();

            dataHashMap.put("username", cursor.getString(cursor.getColumnIndex("username")));
            dataHashMap.put("uid", cursor.getString(cursor.getColumnIndex("uid")));

            data.add(dataHashMap);
        }

        cursor.close();
        super.closeDB();

        return data;
    }

    public class Column implements BaseColumns {
        public static final String ID         = BaseColumns._ID;
        public static final String USERNAME   = "username";
        public static final String PASSWORD   = "password";
        public static final String EMAIL      = "email";
        public static final String TITLE      = "title";
        public static final String NAME       = "name";
        public static final String LASTNAME   = "lastname";
        public static final String UID        = "uid";
        public static final String UPDATED_AT = "updated_at";
    }
}
