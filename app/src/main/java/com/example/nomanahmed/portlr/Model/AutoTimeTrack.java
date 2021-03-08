package com.example.nomanahmed.portlr.Model;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nomanahmed.portlr.Activities.Splash;
import com.example.nomanahmed.portlr.Helper.NotificationPublisher;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;
import com.example.nomanahmed.portlr.Utils.SharedPrefUtils;

import java.util.Calendar;
import java.util.Date;

import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_ENDTIME;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_ISSETSTARTPAGE;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_IS_ACTIVE;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_IS_CALL_LOGGER;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_IS_FIRST_TIME;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_IS_LOCATION_ENALBE;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_LAT;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_LNG;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_NOTIFICATION_TRIGGER;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_PROJECT_ID;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_PROJECT_NAME;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_STARTTIME;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_WORKDAYS;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_WORKLOCATION;

public class AutoTimeTrack {

    private Context mContext;
    
    public boolean isCallLogger;
    public boolean isLocation;
    public boolean isActive;
    public boolean isSetStartPage;
    public boolean[] workdays;
    public String projectID;
    public String projectName;
    public Date startTime;
    public Date endTime;
    public String workLocation;

    public Float lat;
    public Float lng;

    public boolean notificationTriggered;
    public boolean isFirstTime;

    public AutoTimeTrack(Context context) {
        mContext = context;
    }

    public void saveObj() {

        new SharedPrefUtils(mContext).addValue(isCallLogger, PREF_IS_CALL_LOGGER);
        new SharedPrefUtils(mContext).addValue(isLocation, PREF_IS_LOCATION_ENALBE);
        new SharedPrefUtils(mContext).addValue(isActive, PREF_IS_ACTIVE);
        new SharedPrefUtils(mContext).addValue(isSetStartPage, PREF_ISSETSTARTPAGE);
        new SharedPrefUtils(mContext).addValue(workdays, PREF_WORKDAYS);
        new SharedPrefUtils(mContext).addValue(projectID, PREF_PROJECT_ID);
        new SharedPrefUtils(mContext).addValue(projectName, PREF_PROJECT_NAME);
        new SharedPrefUtils(mContext).addValue(startTime, PREF_STARTTIME);
        new SharedPrefUtils(mContext).addValue(endTime, PREF_ENDTIME);
        new SharedPrefUtils(mContext).addValue(workLocation, PREF_WORKLOCATION);
        new SharedPrefUtils(mContext).addValue(lat, PREF_LAT);
        new SharedPrefUtils(mContext).addValue(lng, PREF_LNG);
        new SharedPrefUtils(mContext).addValue(notificationTriggered, PREF_NOTIFICATION_TRIGGER);
        new SharedPrefUtils(mContext).addValue(false, PREF_IS_FIRST_TIME);
    }

    public void loadObj() {
        isCallLogger = new SharedPrefUtils(mContext).getBoolean(PREF_IS_CALL_LOGGER);
        isLocation = new SharedPrefUtils(mContext).getBoolean(PREF_IS_LOCATION_ENALBE);
        isActive = new SharedPrefUtils(mContext).getBoolean(PREF_IS_ACTIVE);
        isSetStartPage = new SharedPrefUtils(mContext).getBoolean(PREF_ISSETSTARTPAGE);
        workdays = new SharedPrefUtils(mContext).getBooleanArray(PREF_WORKDAYS);
        projectID = new SharedPrefUtils(mContext).getValue(PREF_PROJECT_ID);
        projectName = new SharedPrefUtils(mContext).getValue(PREF_PROJECT_NAME);
        startTime = new SharedPrefUtils(mContext).getDate(PREF_STARTTIME);
        endTime = new SharedPrefUtils(mContext).getDate(PREF_ENDTIME);
        workLocation = new SharedPrefUtils(mContext).getValue(PREF_WORKLOCATION);
        lat = new SharedPrefUtils(mContext).getFloat(PREF_LAT);
        lng = new SharedPrefUtils(mContext).getFloat(PREF_LNG);
        notificationTriggered = new SharedPrefUtils(mContext).getBoolean(PREF_NOTIFICATION_TRIGGER);
        isFirstTime = new SharedPrefUtils(mContext).getBoolean(PREF_IS_FIRST_TIME);
    }

    public void clearObj() {
        new SharedPrefUtils(mContext).addValue(false, PREF_IS_CALL_LOGGER);
        new SharedPrefUtils(mContext).addValue(false, PREF_IS_LOCATION_ENALBE);
        new SharedPrefUtils(mContext).addValue(false, PREF_IS_ACTIVE);
        new SharedPrefUtils(mContext).addValue(false, PREF_ISSETSTARTPAGE);
        new SharedPrefUtils(mContext).addValue(new boolean[]{false, false, false, false, false, false, false}, PREF_WORKDAYS);
        new SharedPrefUtils(mContext).addValue("", PREF_PROJECT_ID);
        new SharedPrefUtils(mContext).addValue("", PREF_PROJECT_NAME);
        new SharedPrefUtils(mContext).addValue(DateTimeUtils.getStringToTime("09:00:00"), PREF_STARTTIME);
        new SharedPrefUtils(mContext).addValue(DateTimeUtils.getStringToTime("16:00:00"), PREF_ENDTIME);
        new SharedPrefUtils(mContext).addValue("", PREF_WORKLOCATION);
        new SharedPrefUtils(mContext).addValue(0f, PREF_LAT);
        new SharedPrefUtils(mContext).addValue(0f, PREF_LNG);
        new SharedPrefUtils(mContext).addValue(false, PREF_NOTIFICATION_TRIGGER);
        new SharedPrefUtils(mContext).addValue(true, PREF_IS_FIRST_TIME);

        new CheckInOut(mContext).clearObj();
    }

    public boolean isCheckCallLogger() {
        loadObj();
        return isCallLogger;
    }

    Date getStartTime() {
        loadObj();
        return startTime;
    }

    Date getEndTime() {
        loadObj();
        return endTime;
    }

    public boolean isStartPageSet() {
        return isSetStartPage;
    }

    public void removeNotification(int status) {
        int wday = getWeekDay();

        Log.d("AutoTimeTrack", "" + wday);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(mContext, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext, 1000 * status + wday, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);

    }

    public void removeAllNotifications() {
        for(int i = 1; i <= 7; i ++) {
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(mContext, NotificationPublisher.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    mContext, 1000 + i, myIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.cancel(pendingIntent);
        }

        for(int i = 1; i <= 7; i ++) {
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(mContext, NotificationPublisher.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    mContext, 2000 + i, myIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.cancel(pendingIntent);
        }
    }

    public void registerNotifications() {
        new CheckInOut(mContext).clearObj();
        removeAllNotifications();
        notificationTriggered = false;
        saveObj();

        if (!isActive) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int startH = calendar.get(Calendar.HOUR_OF_DAY);
        int startM = calendar.get(Calendar.MINUTE);
        calendar.setTime(endTime);
        int endH = calendar.get(Calendar.HOUR_OF_DAY);
        int endM = calendar.get(Calendar.MINUTE);

        for(int i = 0; i < 7; i ++) {
            if(workdays[i]) {
                int workday = i + 2;
                if(i == 6) workday = 1;
                scheduleNotification(workday, startH, startM, 1);
                scheduleNotification(workday, endH, endM, 2);
            }
        }

    }

    private void scheduleNotification(int workday, int hour, int minute, int status) {

        Log.d("AutoTimeTrack", "" + workday + ", " + hour + ", " + minute + ", " + status);

        Intent notificationIntent = new Intent(mContext, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1000 * status + workday);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1000 * status + workday, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, workday);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Log.d("AutoTimeTrack", "" + System.currentTimeMillis());
        Log.d("AutoTimeTrack", "" + calendar.getTimeInMillis());

        long time = calendar.getTimeInMillis();

        if(time < System.currentTimeMillis()) {
            time += 7 * 24 * 60 * 60 * 1000;
        }

        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, new Date().getTime(), pendingIntent);
    }

    public int getWeekDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(Calendar.DAY_OF_WEEK);
    }

}
