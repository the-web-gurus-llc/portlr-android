package com.example.nomanahmed.portlr.Activities;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.example.nomanahmed.portlr.Helper.Constants;
import com.example.nomanahmed.portlr.Helper.LocationService;
import com.example.nomanahmed.portlr.Model.AutoTimeTrack;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.SharedPrefUtils;

public class Splash extends AppCompatActivity {
    Constants constants;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startService(new Intent(getApplicationContext(), LocationService.class));

        constants=new Constants(this);
        preferences=getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);
        editor=preferences.edit();
        editor.putString("TodayDate","yes");
        editor.apply();
        editor.commit();
//        Typeface face = Typeface.createFromAsset(getAssets(),
//                "fonts/concertone-regular.ttf");
//        face = Typeface.createFromAsset(getAssets(),
//                "fonts/Roboto-Bold.ttf");
        new Handler().postDelayed(new Runnable() {
            public void run() {
                finish();
                if (preferences.getString("isLogin","").equals("true"))
                {
                    AutoTimeTrack autoTime = new AutoTimeTrack(Splash.this);
                    autoTime.loadObj();

                    if (autoTime.isStartPageSet()) {
                        startActivity(new Intent(Splash.this,TimeClockActivity.class));
                    } else {
                        startActivity(new Intent(Splash.this,RegistrationTimeActivity.class));
                    }
//                    startActivity(new Intent(Splash.this,Login.class));
                }else
                {
                    startActivity(new Intent(Splash.this,Login.class));
                }
            }
        }, 3000);
    }
}
