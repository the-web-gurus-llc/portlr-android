package com.example.nomanahmed.portlr.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.nomanahmed.portlr.Helper.AppController;
import com.example.nomanahmed.portlr.Helper.Constants;
import com.example.nomanahmed.portlr.Model.AutoTimeTrack;
import com.example.nomanahmed.portlr.Model.CheckInOut;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.AlertUtils;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeClockActivity extends AppCompatActivity {

    ImageView settingIV;
    ImageView clockIV;
    ImageView logOutIV;
    ImageView callIV;
    ImageView regBtn;

    Button cancelBtn;
    Button checkInBtn;

    EditText startEt;
    EditText endEt;

    Calendar mStartTime = Calendar.getInstance();
    Calendar mEndTime = Calendar.getInstance();

    AutoTimeTrack autoTimeTrack ;
    CheckInOut checkInOut ;

    String CHECK_IN_STR = "CHECK-IN";
    String CHECK_OUT_STR = "CHECK-OUT";

    Constants constants;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeclock);

        autoTimeTrack = new AutoTimeTrack(getApplicationContext());
        checkInOut = new CheckInOut(this);

        constants=new Constants(this);
        preferences=getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);

        setBottomBtns();
        setBtns();

        startEt = findViewById(R.id.startEt);
        endEt = findViewById(R.id.endEt);
        setTimeListener();

        loadUI();

    }

    void loadUI() {

        checkInOut.loadObj();
        autoTimeTrack.loadObj();

        Date date = checkInOut.date;

        if(DateUtils.isToday(date.getTime())) {
            if(checkInOut.checkout) {
                mStartTime.setTime(checkInOut.startDate);
                updateTime(1);
                if(checkInOut.endDate.getTime() == autoTimeTrack.endTime.getTime()) {
                    endEt.setText("");
                } else {
                    mEndTime.setTime(checkInOut.endDate);
                    updateTime(2);
                }

                startEt.setFocusable(false);
                endEt.setFocusable(false);

                checkInBtn.setText(CHECK_IN_STR);
                return;
            }

            if(checkInOut.checkin) {
                endEt.setText("");
                mStartTime.setTime(checkInOut.startDate);
                updateTime(1);
                startEt.setFocusable(true);
                endEt.setFocusable(true);
                checkInBtn.setText(CHECK_OUT_STR);
                return;
            }
        } else {
            autoTimeTrack.registerNotifications();
        }

        endEt.setText("");
        mStartTime.setTime(checkInOut.startDate);
        updateTime(1);
        startEt.setFocusable(true);
        endEt.setFocusable(false);
        checkInBtn.setText(CHECK_IN_STR);

    }

    void setTimeListener() {
        startEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int hour = mStartTime.get(Calendar.HOUR_OF_DAY);
                int minute = mStartTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TimeClockActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mStartTime.setTime(DateTimeUtils.getStringToTime(selectedHour + ":" + selectedMinute + ":00"));
                        updateTime(1);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();
            }
        });

        endEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = mEndTime.get(Calendar.HOUR_OF_DAY);
                int minute = mEndTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TimeClockActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mEndTime.setTime(DateTimeUtils.getStringToTime(selectedHour + ":" + selectedMinute + ":00"));
                        updateTime(2);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select End Time");
                mTimePicker.show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    void updateTime(int num) {
        if(num == 1) {
            startEt.setText(DateTimeUtils.changeDateFormat(mStartTime.getTime(), "HH:mm"));
        } else {
            endEt.setText(DateTimeUtils.changeDateFormat(mEndTime.getTime(), "HH:mm"));
        }

        if(startEt.getText().toString().equals("") || endEt.getText().toString().equals("")) {
            return;
        }

        Date time1 = mStartTime.getTime();
        Date time2 = mEndTime.getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(time1);
        int sh = cal.get(Calendar.HOUR_OF_DAY);
        int sd = cal.get(Calendar.MINUTE);
        cal.setTime(time2);
        int eh = cal.get(Calendar.HOUR_OF_DAY);
        int ed = cal.get(Calendar.MINUTE);

        long interval = eh * 60 + ed - sh * 60 - sd;

        if(interval < 0) {
            AlertUtils.showAlert(this, "End Time should be greater than Start Time");
        }

    }

    void checkBtnClicked() {

        String btnTitle = checkInBtn.getText().toString();
        if(checkInOut.checkout) {
            AlertUtils.showAlert(this, "Your time is already registered, today.");
            return;
        }

        if(btnTitle.equals(CHECK_IN_STR)) {
            checkInOut.date = new Date();
            checkInOut.checkin = true;
            checkInOut.checkout = false;
            checkInOut.saveObj();
            autoTimeTrack.removeNotification(1);
            loadUI();
        } else {
            registerTimeAutomatically();
        }
    }

    void registerTimeAutomatically() {

        if(endEt.getText().toString().isEmpty()) {
            AlertUtils.showAlert(this, "Please select end time.");
            return;
        }

        Date time1 = mStartTime.getTime();
        Date time2 = mEndTime.getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(time1);
        int sh = cal.get(Calendar.HOUR_OF_DAY);
        int sd = cal.get(Calendar.MINUTE);
        cal.setTime(time2);
        int eh = cal.get(Calendar.HOUR_OF_DAY);
        int ed = cal.get(Calendar.MINUTE);

        long interval = eh * 60 + ed - sh * 60 - sd;

        if(interval < 0) {
            AlertUtils.showAlert(this, "End Time should be greater than Start Time");
        }


        if(autoTimeTrack.projectID.isEmpty()) {
            AlertUtils.showAlert(this, "You didn't select the project. At this time, we can't register the time automatically");
            return;
        }

        long hour = interval / 60;
        long minute = interval % 60;

        String resultStr = "" + hour + ":" + minute;

        String endAt = DateTimeUtils.changeTimeFormatSpe(mEndTime.getTime());
        String startAt = DateTimeUtils.changeTimeFormatSpe(mStartTime.getTime());
        String spent = DateTimeUtils.changeDateFormat(new Date(), "yyyy-MM-dd") + "T" + DateTimeUtils.changeTimeFormat(mStartTime.getTime()) + ".000Z";
        final String[] arrHoursValues = getHoursDetail(resultStr);

        constants.showProgress("Please wait...");
        final int finalTimeSheetId = 0;
        final String finalSelectedProjectId = autoTimeTrack.projectID;
        final String finalSelectedTaskId = "";
        final String finalStartAt = startAt;
        final String finalSpent = spent;
        final String finalEndAt = endAt;



        StringRequest request = new StringRequest(
                Request.Method.POST,
                preferences.getString("URL","")+"/api/services/app/timesheet/createorupdatetimesheet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onResponse:", response);
                        constants.endProgress();

                        JSONObject obj;
                        try
                        {
                            obj = new JSONObject(response);
                            boolean success = obj.getBoolean("success");
                            if(success) {
                                timeRegistered();
                                new AlertDialog.Builder(TimeClockActivity.this)
                                        .setTitle("")
                                        .setMessage("Your time is registered successfully!")
                                        .setCancelable(false)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).show();
                            } else {
                                AlertUtils.showAlert(TimeClockActivity.this, "Please try later");
                            }

                        }catch (Exception EX)
                        {
                            Log.d("error1", "parsejson: ");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        constants.endProgress();
                        // As of f605da3 the following should work
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                System.out.print(res);
                                Toast.makeText(TimeClockActivity.this, res, Toast.LENGTH_LONG).show();
                            } catch (UnsupportedEncodingException e1) {
                                Log.d("err4", "onErrorResponse: ");
                            }
                        }
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @SuppressLint("LongLogTag")
            @Override
            public byte[] getBody()
            {
                JSONObject params= new JSONObject();
                String body = null;
                try
                {
                    if(finalTimeSheetId != 0) { params.put("id", finalTimeSheetId); }
                    params.put("projectId", finalSelectedProjectId);
                    params.put("taskId", finalSelectedTaskId);
                    params.put("userId", preferences.getString("userID", ""));
                    params.put("endedAt", finalEndAt);
                    params.put("hours", DateTimeUtils.getServerTimeForPost(arrHoursValues[0]));
                    params.put("roundedHours", DateTimeUtils.getServerTimeForPost(arrHoursValues[1]));
                    params.put("isInvoice", "false");
                    params.put("notes", "");
                    params.put("spentAt", finalSpent);
                    params.put("startedAt", finalStartAt);

                    body =params.toString();
                } catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try
                {

                    return body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Map< String, String > getHeaders() throws AuthFailureError {
                HashMap< String, String > headers = new HashMap< String, String >();
                headers.put("Content-Type","application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " +preferences.getString("token",""));
                return headers;
            }
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request, "srarequest");

    }

    String[] getHoursDetail(String resultStr) {
        int intervalInt = DateTimeUtils.getIntervalTime(resultStr);
        if(intervalInt == -1) {
            return new String[0];
        }

        int hours = intervalInt / 3600;
        int minute = (intervalInt % 3600) / 60;

        float roundVal = (float)minute;
        String roundedHours = "0";

        if(roundVal <= 15.0) {
            roundedHours = hours + ".15";
        }
        else if(roundVal > 15.0 && roundVal <= 30.0) {
            roundedHours = hours + ".30";
        }
        else if(roundVal > 30.0 && roundVal <= 45.0) {
            roundedHours = hours + ".45";
        }
        else {
            hours = hours + 1;
            roundedHours = hours + ".00";
        }

        String[] res = {resultStr.replace(":", "."), roundedHours};
        return res;
    }

    void timeRegistered() {
        checkInOut.date = new Date();
        checkInOut.checkin = false;
        checkInOut.checkout = false;
        checkInOut.endDate = mEndTime.getTime();
        checkInOut.startDate = mEndTime.getTime();
        checkInOut.saveObj();
        loadUI();
    }

    void cancelBtnClicked() {
        checkInOut.date = new Date();
        checkInOut.checkin = true;
        checkInOut.checkout = true;
        checkInOut.endDate = autoTimeTrack.endTime;
        checkInOut.saveObj();
        autoTimeTrack.removeNotification(1);
        autoTimeTrack.removeNotification(2);
        loadUI();
    }

    void setBtns() {
        cancelBtn = findViewById(R.id.cancelBtn);
        checkInBtn = findViewById(R.id.checkInBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelBtnClicked();
            }
        });

        checkInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBtnClicked();
            }
        });
    }

    void setBottomBtns() {
        clockIV = findViewById(R.id.clockBtn);
        logOutIV = findViewById(R.id.logoutBtn);
        callIV = findViewById(R.id.callBtn);
        settingIV = findViewById(R.id.settingBtn);
        regBtn = findViewById(R.id.regBtn);

        if (new AutoTimeTrack(this).isCheckCallLogger()) {
            callIV.setVisibility(View.VISIBLE);
        } else callIV.setVisibility(View.GONE);

        settingIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimeClockActivity.this,AutoLogSettingActivity.class));
                finish();
            }
        });

        clockIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimeClockActivity.this,RegistrationListActivity.class));
            }
        });

        callIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimeClockActivity.this,Home.class));
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimeClockActivity.this,RegistrationTimeActivity.class));
                finish();
            }
        });

        logOutIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("OtherSettings", 0).edit().clear().apply();
                finish();
                startActivity(new Intent(TimeClockActivity.this,Login.class));
            }
        });
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();
    }

}
