package com.example.tanap.attendcheck.db;

import android.content.Context;
import android.provider.BaseColumns;

public class User extends Table {
    public static final String TABLE = "users";

    public User(Context context) {
        super(context);
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
