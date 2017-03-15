package com.example.tanap.attendcheck.db;

import android.provider.BaseColumns;

public class User extends Table {
    public static final String TABLE = "user";

    public static String getCreateSQL() {
        return "CREATE TABLE IF NOT EXISTS `users` (\n" +
                "  `" + Column.ID + "` int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `username` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                "  `password` varchar(191) COLLATE utf8mb4_unicode_ci DEFAULT '',\n" +
                "  `email` varchar(191) COLLATE utf8mb4_unicode_ci DEFAULT '',\n" +
                "  `title` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                "  `name` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                "  `lastname` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                "  `uid` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                "  `updated_at` timestamp NULL DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`));";
    }

    public class Column implements BaseColumns {
        public static final String ID         = BaseColumns._ID;
        public static final String USERNAME   = "username";
        public static final String PASSWORD   = "password";
        public static final String EMAIL      = "email";
        public static final String TITLE      = "title";
        public static final String NAME       = "name";
        public static final String LASTNAME   = "lastname";
        public static final String uid        = "uid";
        public static final String UPDATED_AT = "updated_at";
    }
}
