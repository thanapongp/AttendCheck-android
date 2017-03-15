package com.example.tanap.attendcheck.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.db.Courses;
import com.example.tanap.attendcheck.db.DB;
import com.example.tanap.attendcheck.db.Periods;
import com.example.tanap.attendcheck.db.Schedules;
import com.example.tanap.attendcheck.db.User;
import com.example.tanap.attendcheck.interfaces.AsyncResponseBoolean;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterTask extends AsyncHttpResponseHandler {

    private static final int HTTP_CONFLICT = 409;
    private static final int HTTP_NOTFOUND = 404;

    private String url;
    private String endPoint;

    private AsyncResponseBoolean responseClass;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    private Context context;

    private String username;
    private String password;
    private String email;

    public RegisterTask(Context context, AsyncResponseBoolean responseClass) {
        this.responseClass = responseClass;
        this.context = context;

        this.client =  new AsyncHttpClient();
        this.url = context.getResources().getString(R.string.app_server_address);
        this.endPoint = context.getResources().getString(R.string.app_server_register_endpoint);
    }

    public void execute(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email    = email;

        RequestParams requestParams = new RequestParams();
        requestParams.add("username", this.username);
        requestParams.add("password", this.password);
        requestParams.add("email",    this.email);

        client.setMaxRetriesAndTimeout(0,0);
        client.post(getEndPoint(), requestParams, this);
    }

    private String getEndPoint() {
        return this.url + this.endPoint;
    }

    @Override
    public void onStart() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage(context.getResources().getString(R.string.login_msg_registering));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        super.onStart();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        JSONObject jsonResponseBody = null;

        try {
            Log.d("Request success", "Try to convert to JSONObject");
            jsonResponseBody = new JSONObject(new String(responseBody));
            Log.d("Request success", "Body: " + jsonResponseBody.toString());

            insertUserDataToDB(jsonResponseBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertUserDataToDB(JSONObject response) throws JSONException {
        SQLiteDatabase db = new DB(context).getWritableDatabase();

        db.beginTransaction();
        try {
            insertDataToUserTable(response, db);
            insertDataToCourseTable(response, db);
            db.setTransactionSuccessful();

            dialog.dismiss();
            responseClass.processFinish(true);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    private void insertDataToUserTable(JSONObject response, SQLiteDatabase db) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(User.Column.USERNAME,   this.username);
        values.put(User.Column.PASSWORD,   this.password);
        values.put(User.Column.EMAIL,      this.email);
        values.put(User.Column.TITLE,      response.getString("title"));
        values.put(User.Column.NAME,       response.getString("name"));
        values.put(User.Column.LASTNAME,   response.getString("lastname"));
        values.put(User.Column.UID,        response.getJSONArray("device")
                                                   .getJSONObject(0).getString("uid"));
        values.put(User.Column.UPDATED_AT, response.getString("updated_at"));

        long newrowid = db.insert(User.TABLE, null, values);
        Log.d("DB Debug:","insertUserDataToDB: " + newrowid);
    }

    private void insertDataToCourseTable(JSONObject response, SQLiteDatabase db) throws JSONException {
        JSONArray coursesArray = response.getJSONArray("enrollments");

        for (int i = 0; i < coursesArray.length(); i++) {
            ContentValues values = new ContentValues();
            JSONObject coursesObject = coursesArray.getJSONObject(i);

            values.put(Courses.Column.CODE, coursesObject.getString("code"));
            values.put(Courses.Column.NAME, coursesObject.getString("name"));
            values.put(Courses.Column.SECTION, coursesObject.getInt("section"));
            values.put(Courses.Column.SEMESTER, coursesObject.getString("semester"));
            values.put(Courses.Column.YEAR, coursesObject.getInt("year"));
            values.put(Courses.Column.LATE_TIME, coursesObject.getInt("late_time"));
            values.put(Courses.Column.UPDATED_AT, coursesObject.getString("updated_at"));

            long newrowid = db.insert(Courses.TABLE, null, values);

            insertDataToSchedulesTable(db, coursesObject, newrowid);
            insertDataToPeriodsTable(db, coursesObject, newrowid);
        }
    }

    private void insertDataToSchedulesTable(SQLiteDatabase db, JSONObject coursesObject, long courseid) throws JSONException {
        JSONArray schedulesArray = coursesObject.getJSONArray("schedules");

        for (int i = 0; i < schedulesArray.length(); i++) {
            ContentValues values = new ContentValues();
            JSONObject scheduleObject = schedulesArray.getJSONObject(i);

            values.put(Schedules.Column.COURSE_ID, courseid);
            values.put(Schedules.Column.ROOM, scheduleObject.getString("room"));
            values.put(Schedules.Column.START_DATE, scheduleObject.getString("start_date"));
            values.put(Schedules.Column.END_DATE, scheduleObject.getString("end_date"));
            values.put(Schedules.Column.UPDATED_AT, scheduleObject.getString("updated_at"));

            db.insert(Schedules.TABLE, null, values);
        }
    }

    private void insertDataToPeriodsTable(SQLiteDatabase db, JSONObject coursesObject, long courseid) throws JSONException {
        JSONArray periodsArray = coursesObject.getJSONArray("periods");

        for (int i = 0; i < periodsArray.length(); i++) {
            ContentValues values = new ContentValues();
            JSONObject periodObject = periodsArray.getJSONObject(i);

            values.put(Periods.Column.COURSE_ID, courseid);
            values.put(Periods.Column.DAY, periodObject.getInt("day"));
            values.put(Periods.Column.START_TIME, periodObject.getString("start_time"));
            values.put(Periods.Column.END_TIME, periodObject.getString("end_time"));
            values.put(Periods.Column.ROOM, periodObject.getString("room"));
            values.put(Periods.Column.UPDATED_AT, periodObject.getString("updated_at"));

            db.insert(Periods.TABLE, null, values);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.d("Request fail", "Code: " + statusCode);
        Log.d("Request fail", "Code: " + error.getMessage());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        if (statusCode == HTTP_CONFLICT) {
            showConflictDialog(dialogBuilder);
        }

        if (statusCode == HTTP_NOTFOUND) {
            showNotFoundDialog(dialogBuilder);
        }
    }

    private void showConflictDialog(AlertDialog.Builder dialogBuilder) {
        dialogBuilder.setTitle("ไม่สามารถลงทะเบียนได้")
                     .setMessage("รหัสนักศึกษานี้ได้ทำการลงทะเบียนแล้ว\n" +
                                 "หากต้องการลงทะเบียนอุปกรณ์ใหม่กรุณากดปุ่มลงทะเบียน")
                    .setPositiveButton("ลงทะเบียน", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                     .setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {}
                     });

        dialogBuilder.create().show();
    }

    private void showNotFoundDialog(AlertDialog.Builder dialogBuilder) {
        dialogBuilder.setTitle("ไม่สามารถลงทะเบียนได้")
                .setMessage("ไม่พบรหัสนักศึกษานี้")
                .setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        dialogBuilder.create().show();
    }

    @Override
    public void onFinish() {
        //dialog.dismiss();
    }
}
