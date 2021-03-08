package com.example.nomanahmed.portlr.Helper;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.nomanahmed.portlr.Model.CurrentLocationManager.locationUpdatingNow;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Location";
    public LocationManager locationManager;
    public MyLocationListener listener;
    Intent intent;
    Timer t;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
          @Override
          public void run()
          {
              Log.v("performOnBackground", "RUN");
              startLocationDetect();
          }
        }, 0, 1000);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        t.cancel();
//        locationManager.removeUpdates(listener);
//        locationUpdatingNow = false;
    }

    void startLocationDetect() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener(getApplicationContext());

        performOnBackgroundThread(new Runnable() {
            @Override
            public void run() {

                if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationUpdatingNow = true;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, (LocationListener) listener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
            }
        });

    }

    public static Handler performOnBackgroundThread(final Runnable runnable) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
        return handler;
    }

}

