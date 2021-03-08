package com.example.nomanahmed.portlr.Helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.nomanahmed.portlr.Activities.Splash;
import com.example.nomanahmed.portlr.Model.AutoTimeTrack;
import com.example.nomanahmed.portlr.Model.CurrentLocationManager;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;

import java.util.Date;

public class MyLocationListener implements LocationListener {

    private Context mContext;
    private String CHANNEL_ID = "Location_Notification";

    public MyLocationListener(Context context) {
        mContext = context;
    }

    public void onLocationChanged(final Location loc) {
        Log.i("*****", "Location changed");

        CurrentLocationManager.lat = (float) loc.getLatitude();
        CurrentLocationManager.lng = (float) loc.getLongitude();

        AutoTimeTrack autoTime = new AutoTimeTrack(mContext);
        autoTime.loadObj();

        if (!autoTime.isCallLogger && !autoTime.isLocation) {
            return;
        }

        if (autoTime.workLocation.isEmpty()) {return;}
        if (DateTimeUtils.timeCompare(autoTime.startTime, new Date())) {return;}
        if (DateTimeUtils.timeCompare(new Date(), autoTime.endTime)) {return;}

        int wday = autoTime.getWeekDay();
        int weekday = 1;
        if (wday == 1) {weekday = 6;} else weekday = wday - 2;
        if (!autoTime.workdays[weekday]) { return; }
        if(autoTime.notificationTriggered) {return;}

        Log.i("*****", "notification first check");

         /*
        let currentCoordinate = CLLocation(latitude: autoTime.lat, longitude: autoTime.lng)
        let updatedCoordinate = CLLocation(latitude: userLocation.coordinate.latitude, longitude: userLocation.coordinate.longitude)
        let distanceInMeters = currentCoordinate.distance(from: updatedCoordinate)
        print("distance: \(distanceInMeters)")
        if distanceInMeters < 100 {
            return
        }
         */

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(autoTime.lat);
        startPoint.setLongitude(autoTime.lng);

        Location endPoint=new Location("locationB");
        endPoint.setLatitude(loc.getLatitude());
        endPoint.setLongitude(loc.getLongitude());

        double distance = startPoint.distanceTo(endPoint);

         if(distance < 100) {
             return;
         }

         autoTime.notificationTriggered = true;
         autoTime.saveObj();



        Intent intent = new Intent(mContext, Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle("You have left your location" + DateTimeUtils.getCusTime() + ", Check-out?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Portlr", importance);
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(999, builder.build());
            }

        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
            notificationManager.notify(999, builder.build());
        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderDisabled(String provider) {
//            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
    }


    public void onProviderEnabled(String provider) {
//            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
    }
}
