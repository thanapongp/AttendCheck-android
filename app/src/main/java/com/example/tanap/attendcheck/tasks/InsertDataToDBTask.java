package com.example.tanap.attendcheck.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.tanap.attendcheck.db.Attendances;
import com.example.tanap.attendcheck.db.Courses;
import com.example.tanap.attendcheck.db.DB;
import com.example.tanap.attendcheck.db.Periods;
import com.example.tanap.attendcheck.db.Schedules;
import com.example.tanap.attendcheck.db.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InsertDataToDBTask {

    public boolean insertUserDataToDBForTheFirstTime(
            JSONObject response, Context context, String username, String password, String email
    ) {
        SQLiteDatabase db = new DB(context).getWritableDatabase();

        db.beginTransaction();
        try {
            insertDataToUserTableForTheFirstTime(response, db, username, password, email);
            insertDataToCourseTable(response, db);
            db.setTransactionSuccessful();

            return true;
        } catch (SQLiteException | JSONException e) {
            e.printStackTrace();
            return false;

        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean updateDataInDB(JSONObject response, Context context) {

        new Attendances(context).truncate();
        new Courses(context).truncate();
        new Periods(context).truncate();
        new Schedules(context).truncate();

        SQLiteDatabase db = new DB(context).getWritableDatabase();

        db.beginTransactionNonExclusive();

        try {
            insertDataToCourseTable(response, db);
            insertDataToAttendancesTable(response, db);

            db.setTransactionSuccessful();

            return true;
        } catch (SQLiteException | JSONException e) {
            e.printStackTrace();

            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertDataToUserTableForTheFirstTime(
            JSONObject response, SQLiteDatabase db, String username, String password, String email
    )
            throws JSONException, SQLiteException {
        ContentValues values = new ContentValues();
        values.put(User.Column.USERNAME, username);
        values.put(User.Column.PASSWORD, password);
        values.put(User.Column.EMAIL, email);
        values.put(User.Column.TITLE, response.getString("title"));
        values.put(User.Column.NAME, response.getString("name"));
        values.put(User.Column.LASTNAME, response.getString("lastname"));
        values.put(User.Column.UID, response.getJSONArray("device")
                .getJSONObject(0).getString("uid"));
        values.put(User.Column.UPDATED_AT, response.getString("updated_at"));

        long newrowid = db.insert(User.TABLE, null, values);
        Log.d("DB Debug:", "insertUserDataToDB: " + newrowid);
    }

    public void insertDataToCourseTable(JSONObject response, SQLiteDatabase db)
            throws JSONException, SQLiteException {
        JSONArray coursesArray = response.getJSONArray("enrollments");

        for (int i = 0; i < coursesArray.length(); i++) {
            ContentValues values = new ContentValues();
            JSONObject coursesObject = coursesArray.getJSONObject(i);

            values.put(Courses.Column.ID, coursesObject.getInt("id"));
            values.put(Courses.Column.CODE, coursesObject.getString("code"));
            values.put(Courses.Column.NAME, coursesObject.getString("name"));
            values.put(Courses.Column.SECTION, coursesObject.getInt("section"));
            values.put(Courses.Column.SEMESTER, coursesObject.getString("semester"));
            values.put(Courses.Column.YEAR, coursesObject.getInt("year"));
            values.put(Courses.Column.LATE_TIME, coursesObject.getInt("late_time"));
            values.put(Courses.Column.UPDATED_AT, coursesObject.getString("updated_at"));

            db.insert(Courses.TABLE, null, values);

            insertDataToSchedulesTable(db, coursesObject);
            insertDataToPeriodsTable(db, coursesObject);
        }
    }

    public void insertDataToSchedulesTable(SQLiteDatabase db, JSONObject coursesObject)
            throws JSONException, SQLiteException {
        JSONArray schedulesArray = coursesObject.getJSONArray("schedules");

        for (int i = 0; i < schedulesArray.length(); i++) {
            ContentValues values = new ContentValues();
            JSONObject scheduleObject = schedulesArray.getJSONObject(i);

            values.put(Schedules.Column.ID, scheduleObject.getInt("id"));
            values.put(Schedules.Column.COURSE_ID, scheduleObject.getInt("course_id"));
            values.put(Schedules.Column.ROOM, scheduleObject.getString("room"));
            values.put(Schedules.Column.START_DATE, scheduleObject.getString("start_date"));
            values.put(Schedules.Column.END_DATE, scheduleObject.getString("end_date"));
            values.put(Schedules.Column.UPDATED_AT, scheduleObject.getString("updated_at"));

            db.insert(Schedules.TABLE, null, values);
        }
    }

    public void insertDataToPeriodsTable(SQLiteDatabase db, JSONObject coursesObject)
            throws JSONException, SQLiteException {
        JSONArray periodsArray = coursesObject.getJSONArray("periods");

        for (int i = 0; i < periodsArray.length(); i++) {
            ContentValues values = new ContentValues();
            JSONObject periodObject = periodsArray.getJSONObject(i);

            values.put(Periods.Column.ID, periodObject.getInt("id"));
            values.put(Periods.Column.COURSE_ID, periodObject.getInt("course_id"));
            values.put(Periods.Column.DAY, periodObject.getInt("day"));
            values.put(Periods.Column.START_TIME, periodObject.getString("start_time"));
            values.put(Periods.Column.END_TIME, periodObject.getString("end_time"));
            values.put(Periods.Column.ROOM, periodObject.getString("room"));
            values.put(Periods.Column.UPDATED_AT, periodObject.getString("updated_at"));

            db.insert(Periods.TABLE, null, values);
        }
    }

    public void insertDataToAttendancesTable(JSONObject response, SQLiteDatabase db)
        throws JSONException, SQLiteException {
        JSONArray attendancesArray = response.getJSONArray("attendances");

        for (int i = 0; i < attendancesArray.length(); i++) {
            ContentValues values = new ContentValues();
            JSONObject attendanceObject = attendancesArray.getJSONObject(i);

            JSONObject pivotObject = attendanceObject.getJSONObject("pivot");

            values.put(Attendances.Column.SCHEDULE_ID, pivotObject.getInt("schedule_id"));

            db.insert(Attendances.TABLE, null, values);
        }
    }
}