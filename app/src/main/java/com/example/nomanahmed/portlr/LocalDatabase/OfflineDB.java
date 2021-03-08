package com.example.nomanahmed.portlr.LocalDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.nomanahmed.portlr.DataProviders.OfflineDBProvider;

import java.util.ArrayList;
/**
 * Created by Apple on 8/13/2016.
 */
public class OfflineDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "calls.db";
    public static final String TABLEname = "calls";
    public static final String ID = "ID";
    public static final String CN = "CN";
    public static final String NUMBER = "Number";
    public static final String DURATION = "Duration";
    public static final String STAtUS = "Status";
    public static final String DATE = "DATE";
    public static final String STIME = "STIME";
    public static final String ETIME = "ETIME";
    public static final String COMPNAME = "COMPNAME";
    public static final String TIMEREGISTERED = "TIMEREGISTERED";
    public static final String CLIENTID = "CLIENTID";
    public static final String TASKID = "TASKID";
    public static final String PROJECTID = "PROJECTID";
    public static final String TASKNAME = "TASKNAME";
    public static final String PROJECTNAME = "PROJECTNAME";
    public static final String ISDELETE = "ISDELETE";
    public static final String CALLTYPE = "CALLTYPE";
    public OfflineDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE =
                "CREATE TABLE " +
                        TABLEname + "("
                        +   ID  +             " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                        + NUMBER +            " Text,"
                        + CN +                " Text,"
                        +   DURATION  +       " Text,"
                        +   STAtUS  +         " Text,"
                        +   DATE  +           " Text,"
                        +   STIME  +          " Text,"
                        +   ETIME  +          " Text,"
                        +   COMPNAME  +       " Text,"
                        +   TIMEREGISTERED  + " Text,"
                        +   CLIENTID  +       " Text,"
                        +   TASKID  +       " Text,"
                        +   PROJECTID  +       " Text,"
                        +   TASKNAME  +       " Text,"
                        +   PROJECTNAME  +       " Text,"
                        +   ISDELETE  +       " Text,"
                        +   CALLTYPE +        " Text"+ ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
        Log.d("success", "table created");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLEname);
        onCreate(db);
    }
    public void deleteAll() {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLEname);
        db.close();
    }
    public void update(String id, String compname, String status,String clientid) {
        ContentValues data=new ContentValues();
        data.put(COMPNAME,compname);
        data.put(STAtUS,status);
        data.put(CLIENTID,clientid);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLEname, data, ID+"=" + id, null);
    }
    public void updateRegistration(String id, String projectId, String taskId, String projectName, String taskName) {
        ContentValues data=new ContentValues();
        data.put(TIMEREGISTERED,"true");
        data.put(PROJECTID,projectId);
        data.put(TASKID,taskId);
        data.put(PROJECTNAME, projectName);
        data.put(TASKNAME, taskName);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLEname, data, ID+"=" + id, null);
    }
    public void deleteRegistration(String id) {
        ContentValues data=new ContentValues();
        data.put(ISDELETE,"true");
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLEname, data, ID+"=" + id, null);
    }
    public void updateByName(String name, String compname, String status) {

        Log.d(name, "updateByName: ");
        Log.d(compname, "updateByName: ");
        Log.d(status, "updateByName: ");
            ContentValues data = new ContentValues();
            data.put(COMPNAME, compname);
            data.put(STAtUS, status);
            SQLiteDatabase db = this.getWritableDatabase();
            db.update(TABLEname, data, CN+"=" + name, null);
            Log.d("success", "updateByName: ");
    }
    public ArrayList<OfflineDBProvider> getdata() {
        ArrayList<OfflineDBProvider> dataProviders = new ArrayList<>();
        String query = "Select * FROM " + TABLEname ;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        if (cursor!=null&&cursor.getCount()>0)
        {
            //while (cursor.moveToNext()) {
                if (cursor.moveToLast())
                {
                    do{
                    OfflineDBProvider provider = new OfflineDBProvider(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cursor.getString(10),
                            cursor.getString(11),
                            cursor.getString(12),
                            cursor.getString(13),
                            cursor.getString(14),
                            cursor.getString(15),
                            cursor.getString(16)
                    );
                      /*  Log.d("Get DB SQLITE", "getdata: ");
                        Log.d(cursor.getString(0), "ID");
                        Log.d(cursor.getString(1), "Number:");
                        Log.d(cursor.getString(2), "Name:");
                        Log.d(cursor.getString(3), "Duration:");
                        Log.d(cursor.getString(4), "Status:");
                        Log.d(cursor.getString(5), "Date:");
                        Log.d(cursor.getString(6), "StartTime:");
                        Log.d(cursor.getString(7), "EndTime:");
                        Log.d(cursor.getString(8), "Compname:");
                        Log.d(cursor.getString(10), "ClientID:");
                        Log.d(cursor.getString(9), "TimeRegistered:");
                        Log.d(cursor.getString(11), "Calltype:");
                        */
                      if(provider.getIsDelete().equals("false"))
                        dataProviders.add(provider);

                    }while (cursor.moveToPrevious());
                }
                //}
        }
        return dataProviders;
    }
    public OfflineDBProvider getdataByID(String id) {
        Log.d(id+"=ID", "getdataByID:");
        OfflineDBProvider provider=new OfflineDBProvider();
        String query = "Select * FROM " + TABLEname + " WHERE " + ID + " = " + id;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext())
        {
             provider=new OfflineDBProvider(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                     cursor.getString(11),
                     cursor.getString(12),
                     cursor.getString(13),
                     cursor.getString(14),
                     cursor.getString(15),
                     cursor.getString(16)
            );
             /*
            Log.d("DB SQLITE", "getdataByID: ");
            Log.d(cursor.getString(0), "ID");
            Log.d(cursor.getString(1), "Number:");
            Log.d(cursor.getString(2), "Name:");
            Log.d(cursor.getString(3), "Duration:");
            Log.d(cursor.getString(4), "Date:");
            Log.d(cursor.getString(5), "Status:");
            Log.d(cursor.getString(6), "StartTime:");
            Log.d(cursor.getString(7), "EndTime:");
            Log.d(cursor.getString(8), "Compname:");
            Log.d(cursor.getString(10), "ClientID:");
            Log.d(cursor.getString(9), "TimeRegistered:");
            Log.d(cursor.getString(11), "Calltype:");
*/
        }
        return provider;
    }
    public void addProduct(OfflineDBProvider Task) {
        ContentValues values = new ContentValues();
        values.put(CN, Task.getName());
        values.put(NUMBER, Task.getNumber());
        values.put(STAtUS, Task.getStatus());
        values.put(DATE, Task.getDate());
        values.put(DURATION, Task.getDuration());
        values.put(STIME, Task.getStarttime());
        values.put(ETIME, Task.getEndtime());
        values.put(COMPNAME, Task.getCompname());
        values.put(TIMEREGISTERED, Task.getTimeregistered());
        values.put(CLIENTID, Task.getClientID());
        values.put(TASKID, Task.getTaskID());
        values.put(PROJECTID, Task.getProjectID());
        values.put(TASKNAME, Task.getTaskName());
        values.put(PROJECTNAME, Task.getProjectName());
        values.put(ISDELETE, Task.getIsDelete());
        values.put(CALLTYPE, Task.getCalltype());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLEname, null, values);
        db.close();
        /*
        Log.d("Success", "Record inserted ");
        Log.d(Task.getName(), "Name:");
        Log.d(Task.getNumber(), "Number:");
        Log.d(Task.getStatus(), "Status:");
        Log.d(Task.getDate(), "Date:");
        Log.d(Task.getDuration(), "Duaration:");
        Log.d(Task.getStarttime(), "StartTime:");
        Log.d(Task.getEndtime(), "EndTime:");
*/
    }
}