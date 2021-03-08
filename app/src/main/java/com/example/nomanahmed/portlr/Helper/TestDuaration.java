package com.example.nomanahmed.portlr.Helper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.nomanahmed.portlr.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TestDuaration extends AppCompatActivity {
    ////////////////////////////////////////
    String dateStart = "1/14/2012 09:5:5";
    String dateStop = "1/15/2019 10:5:5";
    SimpleDateFormat format;
    Date d1 = null;
    Date d2 = null;
    ////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_duaration);
        final Button start=(Button)findViewById(R.id.start);
        Button stop=(Button)findViewById(R.id.stop);
        format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DateFormat df=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    dateStart=df.format(Calendar.getInstance().getTime());
                    Log.d(dateStart, "StartDate");
                    d1 = format.parse(dateStart);
                }catch (Exception ex)
                {
                    Log.d("error", "onClick: ");
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DateFormat df=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    dateStop=df.format(Calendar.getInstance().getTime());
                    Log.d(dateStop, "StopDate");
                    d2 = format.parse(dateStop);
                    long diff = d2.getTime() - d1.getTime();
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    Log.d(diffDays+"", "days: ");
                    Log.d(diffHours+"", "hours: ");
                    Log.d(diffMinutes+"", "mints: ");
                    Log.d(diffSeconds+"", "seconds: ");

                }catch (Exception ex)
                {
                    Log.d("Error 2", "onClick");
                }
            }
        });
    }

}
