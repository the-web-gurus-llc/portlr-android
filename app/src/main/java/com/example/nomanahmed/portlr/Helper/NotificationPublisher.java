package com.example.nomanahmed.portlr.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.nomanahmed.portlr.Activities.Splash;
import com.example.nomanahmed.portlr.Model.AutoTimeTrack;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;

public class NotificationPublisher extends BroadcastReceiver {

    public static String CHANNEL_ID = "Notification";
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent fIntent) {


        int id = fIntent.getIntExtra(NOTIFICATION_ID, 0);

        Log.d("AutoTimeTrack", "-> " + id);

        AutoTimeTrack autoTimeTrack = new AutoTimeTrack(context);
        autoTimeTrack.loadObj();

        String startTimeNotification = "Time is " + DateTimeUtils.getNotificationTime(autoTimeTrack.startTime) + " - Check-in?";
        String endTimeNotification = "Time is " + DateTimeUtils.getNotificationTime(autoTimeTrack.endTime) + " - Check-out?";
        String content = "";
        if(id < 2000) content = startTimeNotification;
        else content = endTimeNotification;

        Intent intent = new Intent(context, Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Portlr", importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(id, builder.build());
            }

        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(id, builder.build());
        }

    }
}
