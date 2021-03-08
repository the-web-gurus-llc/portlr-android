package com.example.nomanahmed.portlr.Helper;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.example.nomanahmed.portlr.DataProviders.OfflineDBProvider;
import com.example.nomanahmed.portlr.LocalDatabase.OfflineDB;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class InComingCallReceiver extends BroadcastReceiver {
    Constants constants;
    String PhoneNumber;
    String dateStart ;
    String dateStop ;
    SimpleDateFormat format;
    Date d1 = null;
    Date d2 = null;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    public void onReceive( Context context, Intent intent) {
        constants=new Constants(context);
          format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        StartOperation(context,intent);
    }
    private void StartOperation(final Context context,Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            PhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (!constants.getValue("NumberFound").equals("YES")) {
                if (validCellPhone(PhoneNumber)) {
                    Log.d("Valid Number", "onReceive: ");
                    constants.addValue("PhoneNumber", PhoneNumber);
                    constants.addValue("NumberFound", "YES");
                } else {
                    Log.d("Invalid Number", "onReceive: ");
                }
            }
            // Log.d(PhoneNumber, "onReceive: ");
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                Log.d("Ringing", "onReceive: ");
                constants.addValue("Ringing", "YES");
            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)))
            {
                //if (constants.getValue("Ringing").equals("YES"))
                //{
                    Log.d("True", "Call picked ");
                    constants.addValue("CallPicked", "YES");
                    if (!constants.getValue("NumberFound").equals("YES"))
                    {
                        constants.addValue("PhoneNumber", PhoneNumber);
                        Log.d("phone number", "YES");
                    }
                    // start time
                    try {
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        dateStart = df.format(Calendar.getInstance().getTime());
                        Log.d(dateStart, "StartDate");
                        constants.addValue("StartDate", dateStart);
                    } catch (Exception ex) {
                        Log.d("error", "onClick: ");
                    }
                //}
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {
                Log.d("Call ended", "StartOperation: ");
                if (constants.getValue("CallPicked").equals("YES"))
                {
                    Log.d("true", "call ended  ");
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //End time
                    String duration = "";
                    DateFormat dfa = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    dateStop = dfa.format(Calendar.getInstance().getTime());
                    d1 = format.parse(constants.getValue("StartDate"));
                    Log.d(dateStop, "StopDate");
                    d2 = format.parse(dateStop);
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //duration
                    long diff = d2.getTime() - d1.getTime();
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000) % 24;
//                    long diffDays = diff / (24 * 60 * 60 * 1000);

                    String strHours = String.valueOf(diffHours);
                    String strMinutes = String.valueOf(diffMinutes);
                    String strSeconds = String.valueOf(diffSeconds);

                    if(strHours.length() == 1) strHours = "0" + strHours;
                    if(strMinutes.length() == 1) strMinutes = "0" + strMinutes;
                    if(strSeconds.length() == 1) strSeconds = "0" + strSeconds;

//                    if (diffDays != 0) {
//                        duration = diffDays + "s:" + diffHours + "h:" + diffMinutes + "m:" + diffSeconds + "s";
//                    } else if (diffHours != 0) {
//                        duration = diffHours + "h:" + diffMinutes + "m:" + diffSeconds + "s";
//                    } else if (diffMinutes != 0) {
//                        duration = diffMinutes + "m:" + diffSeconds + "s";
//                    } else if (diffSeconds != 0) {
//                        duration = diffSeconds + "s";
//                    }
                    duration = strHours + ":" + strMinutes + ":" + strSeconds;
                    constants.addValue("TEMP_DURATION",duration);
                    // Log.d(duration, "Call Duration ");
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////



                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //fetching date
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c);
                    constants.addValue("TEMP_DATE",formattedDate);
                    // Log.d(formattedDate, "Date");
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



//                    String name = getContactDisplayNameByNumber("", context);
                    String name = "Incoming call from";
                    constants.addValue("TEMP_NAME",name);
                    if (constants.getValue("Ringing").equals("YES"))
                    {

                        Log.d("Ringing YES", "StartOperation:");
                             if (name.equals("Unknown number")){
                                 Log.d("Client Not Registered", "False");
                                 insertRecord(context,constants.getValue("PhoneNumber"),"Unknown",duration,"Unknown",formattedDate,constants.getValue("StartDate"),dateStop,"Null","false","NULL","", "", "", "","false","Incoming");
                             }
                             else {
                                 Log.d("Client is Registered", "True");
                                 if (name.contains(" "))
                                 {
                                     name = name.replace(" ", "_");
                                 }
                                 insertRecord(context,constants.getValue("PhoneNumber"),name,duration,"Unknown",formattedDate,constants.getValue("StartDate"),dateStop,"Null","false","NULL","", "", "", "","false", "Incoming");
                             }
                    }else
                    {
                        Log.d("Handler initiated", "StartOperation:");
                        if (!constants.getValue("HANDLER").equals("ON"))
                        {
                            constants.addValue("HANDLER","ON");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    String time = constants.getValue("TEMP_DURATION");
                                    insertRecord(context, constants.getValue("PhoneNumber"), "Unknown", time, "Unknown", constants.getValue("TEMP_DATE"), constants.getValue("StartDate"), dateStop, "Null", "false", "NULL","", "","", "", "false", "Outgoing");

/*
                                    Log.d(time, "run:Duration ");
                                    if (time.equals("NULL"))
                                     {
                                      Log.d("NULL found", "run: ");
                                      time = constants.getValue("TEMP_DURATION");
                                    }else {
                                     time=getRealTime(time);
                                    }
                                    if (time.equals("0s")) {
                                      Log.d("outgoing missed", "call ");
                                       constants.remove();
                                    }else {
                                        if (constants.getValue("TEMP_NAME").equals("Unknown number")) {
                                            Log.d("Client Not Registered", "False");
                                            insertRecord(context, constants.getValue("PhoneNumber"), "Unknown", time, "Unknown", constants.getValue("TEMP_DATE"), constants.getValue("StartDate"), dateStop, "Null", "false", "NULL","", "","", "", "false", "Outgoing");
                                        } else {
                                            String newname = constants.getValue("TEMP_NAME");
                                            Log.d("Client is Registered", "True");
                                            if (newname.contains(" ")) {
                                                newname = newname.replace(" ", "_");
                                            }
                                            insertRecord(context, constants.getValue("PhoneNumber"), newname, time, "Unknown", constants.getValue("TEMP_DATE"), constants.getValue("StartDate"), dateStop, "Null", "false", "NULL","", "", "", "", "false", "Outgoing");
                                        }
                                    }
                                    */
                                }
                            }, 5000);
                        }
                    }
                } else
                {
                     Log.d("Clearing constants", "StartOperation: ");
                           constants.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("error", "onReceive: ");
        }
    }
    //function to insert data into sqlite for recent call
    private void insertRecord(Context context,String number,String name,String duration,String status,String date,String startdate,String stopdate,String compname,String timeregstered,String clientID, String taskID, String projectID, String taskName, String projectName, String isDelete, String calltype) {
        preferences=context.getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);
        if (preferences.getString("isLogin","").equals("true")) {
            Log.d("records", "insertRecord: ");
            OfflineDB db = new OfflineDB(context, null, null, Constants.DBVERSION);
            OfflineDBProvider provider = new OfflineDBProvider("", number, name, duration, status, date, startdate, stopdate, compname, timeregstered, clientID, taskID, projectID, taskName, projectName, isDelete, calltype);
            db.addProduct(provider);
        }else
        {
            Log.d("Login Failed", "insertRecord: ");
        }
        constants.remove();
      }
    private String getRealTime(String time) {
        int tot_seconds = Integer.parseInt(time);
        int hours = tot_seconds / 3600;
        int minutes = (tot_seconds % 3600) / 60;
        int seconds = tot_seconds % 60;
        String finatime="";
        String m=minutes+"";
        String h=hours+"";
        String s=seconds+"";
        Log.d(h+":"+m+":"+s, "getRealTime: ");
        if (m.equals("0")&&h.equals("0"))
        {
                finatime=s+"s";
        }else if (h.equals("0")&&!m.equals("0"))
        {

            finatime=m+"m:"+s+"s";
        }else if (!h.equals("0"))
        {

            finatime=h+"h:"+m+"m:"+s+";";
        }
        String timeString = String.format("%02dh%02dm%02ds", hours, minutes, seconds);
        return finatime;
    }
    //Function to check the number registration status on phonebook
    private String getRawContactId(String contactId,Context context) {
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{contactId};
        ContentResolver mContentResolver=context.getContentResolver();
        Cursor c = mContentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (c == null) return null;
        int rawContactId = -1;
        if (c.moveToFirst()) {
            rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
        }
        c.close();
        return String.valueOf(rawContactId);

    }
    private String getCompanyName(String rawContactId,Context context) {
        try {
            String orgWhere = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] orgWhereParams = new String[]{rawContactId,
                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
            ContentResolver mContentResolver=context.getContentResolver();
            Cursor cursor = mContentResolver.query(ContactsContract.Data.CONTENT_URI,
                    null, orgWhere, orgWhereParams, null);

            if (cursor == null) return null;
            String name = null;
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
            }
            cursor.close();
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /*
    public String getContactDisplayNameByNumber(String number,Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(constants.getValue("PhoneNumber")));
        String name = "Incoming call from";
        String comp_name="";
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, null, null, null, null);
        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));


                    // String rawContactId = getRawContactId(contactId,context);
               // String companyName = getCompanyName(rawContactId,context);
               // Log.d(companyName, "getContactCompany: ");
                // this.id =
                // contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                // String contactId =
                // contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }else{
                name = "Unknown number";
            }
        }catch (Exception ex)
        {
            Log.d("*Error*", "getContactDisplayNameByNumber: ");
        }
        finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }
        return name;
    }
    */

    public boolean validCellPhone(String number) {
        try
        {
            return android.util.Patterns.PHONE.matcher(number).matches();
        }catch (Exception ex)
        {
        return false;
        }
    }
/*
    private  String  getLastCallDetails(Context context,String phoneNumber) {
        Uri contacts = CallLog.Calls.CONTENT_URI;
        String time="NULL";
        try {
            Cursor managedCursor = context.getContentResolver().query(contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                while (managedCursor.moveToNext())
                {
                    String phNumber = managedCursor.getString(number);
                    String callDate = managedCursor.getString(date);
                    String callDayTime = new Date(Long.valueOf(callDate)).toString();
                    String callDuration = managedCursor.getString(duration);
                    Log.d(phNumber, "Number");
                    Log.d(callDuration, "duration");
                    Log.d(callDayTime, "datetime");
                    if (phNumber.equals(phoneNumber))
                        time=callDuration+"";
                }
            managedCursor.close();
        } catch (SecurityException e) {
            Log.e("Security Exception", "User denied call log permission");
        }
          return time;
        }
 */
}
