package com.example.nomanahmed.portlr.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.nomanahmed.portlr.Adapters.MySpinnerAdapter;
import com.example.nomanahmed.portlr.Helper.AppController;
import com.example.nomanahmed.portlr.Helper.Background_Call_Det_Service;
import com.example.nomanahmed.portlr.Helper.Constants;
import com.example.nomanahmed.portlr.Helper.LocationService;
import com.example.nomanahmed.portlr.Model.AutoTimeTrack;
import com.example.nomanahmed.portlr.Model.CurrentLocationManager;
import com.example.nomanahmed.portlr.Model.Project;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.AlertUtils;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;
import com.example.nomanahmed.portlr.Utils.Functions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.nomanahmed.portlr.Model.CurrentLocationManager.locationUpdatingNow;

public class AutoLogSettingActivity extends AppCompatActivity implements LocationListener {

    private final String TAG = "AutoLogSettingActivity";

    private LocationManager locationManager;
//    private Location onlyOneLocation;
    private final int REQUEST_FINE_LOCATION = 1234;


    CheckBox callLogCheckBox;
    CheckBox activateCheckBox;
    CheckBox startPageCheckBox;
    CheckBox locationCheckBox;

    TextView mBtn;
    TextView tueBtn;
    TextView wesBtn;
    TextView thuBtn;
    TextView friBtn;
    TextView satBtn;
    TextView sunBtn;

    Spinner productSpin;
    EditText projectEt;
    View projectView;

    EditText startEt;
    EditText endEt;

    Calendar mStartTime = Calendar.getInstance();
    Calendar mEndTime = Calendar.getInstance();

    List<String> projectList = new ArrayList<>();
    MySpinnerAdapter projectSpinnerArrayAdapter;

    List<Project> projectObList = new ArrayList<>();

    boolean fistTime = true;

    Constants constants;
    SharedPreferences preferences;

    TextView activatedLocationLb;

    Button confirmBtn;

    ImageView settingIV;
    ImageView clockIV;
    ImageView logOutIV;
    ImageView callIV;
    ImageView timeClockBtn;
    ImageView regBtn;

    Button setNewWorkLocationBtn;

    String locationStr = "";
    float lat = 0f;
    float lng = 0f;
    boolean isSetLocation = false;

    boolean[] workdays = new boolean[]{false, false, false, false, false, false, false};
    AutoTimeTrack autoTimeTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autologsetting);

        autoTimeTrack = new AutoTimeTrack(getApplicationContext());

        locationDetect();


        constants=new Constants(this);
        preferences=getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);

        findViewByIds();

        startEt = findViewById(R.id.startEt);
        endEt = findViewById(R.id.endEt);
        setTimeListener();

        setBottomBtns();
        showPhoneCallLoggerBtn();
        setSpins();

        loadUI();
        loadProject();
        setUpWeeks();
        setUpTimes();
        setUpSetLocationButton();
    }

    void confirmClicked() {
        autoTimeTrack.isCallLogger = callLogCheckBox.isChecked();
        autoTimeTrack.isActive = activateCheckBox.isChecked();
        autoTimeTrack.isSetStartPage = startPageCheckBox.isChecked();
        autoTimeTrack.isLocation = locationCheckBox.isChecked();

        if(!startEt.getText().toString().equals("")) {
            autoTimeTrack.startTime = mStartTime.getTime();
        }

        if(!endEt.getText().toString().equals("")) {
            autoTimeTrack.endTime = mEndTime.getTime();
        }

        System.arraycopy(workdays, 0, autoTimeTrack.workdays, 0, 7);

        if(isSetLocation) {
            autoTimeTrack.workLocation = activatedLocationLb.getText().toString();
            autoTimeTrack.lat = lat;
            autoTimeTrack.lng = lng;
        } else {
            if(activatedLocationLb.getText().toString().isEmpty()) {
                autoTimeTrack.workLocation = "";
                autoTimeTrack.lat = 0f;
                autoTimeTrack.lng = 0f;
            }
        }

        String selectedProjectName = projectEt.getText().toString();

        if(selectedProjectName.equals("")) {
            autoTimeTrack.projectID = "";
            autoTimeTrack.projectName = "";
        } else {
            ArrayList<Project> selectedProjects = filterProjects(selectedProjectName);
            if(selectedProjects.size() != 0) {
                int selectedId = selectedProjects.get(0).userId;
                autoTimeTrack.projectID = String.valueOf(selectedId);
                autoTimeTrack.projectName = selectedProjectName;
            }
        }

        autoTimeTrack.saveObj();

        autoTimeTrack.registerNotifications();

        new AlertDialog.Builder(AutoLogSettingActivity.this)
                .setTitle("")
                .setMessage("Registered successfully")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showPhoneCallLoggerBtn();
                    }
                }).show();

    }

    void showPhoneCallLoggerBtn() {
        if (new AutoTimeTrack(this).isCheckCallLogger()) {
            callIV.setVisibility(View.VISIBLE);
        } else callIV.setVisibility(View.GONE);
    }

    void onClickSetNewWorkLocation() {
        isSetLocation = true;
        lat = CurrentLocationManager.lat;
        lng = CurrentLocationManager.lng;

        Log.d("Location -> ", "" + lat + ", " + lng);

        Geocoder geocoder = new Geocoder(AutoLogSettingActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses.size() > 0) {
                locationStr = addresses.get(0).getAddressLine(0);
                activatedLocationLb.setText(locationStr);
            }
        } catch (IOException e) {
            locationStr = "";
            activatedLocationLb.setText(locationStr);
        }
    }

    void setBottomBtns() {
        clockIV = findViewById(R.id.clockBtn);
        logOutIV = findViewById(R.id.logoutBtn);
        callIV = findViewById(R.id.callBtn);
        timeClockBtn = findViewById(R.id.timeClockBtn);
        settingIV = findViewById(R.id.settingBtn);
        regBtn = findViewById(R.id.regBtn);
        setNewWorkLocationBtn = findViewById(R.id.setNewWorkLocationBtn);

        setNewWorkLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSetNewWorkLocation();
            }
        });

        showPhoneCallLoggerBtn();

        timeClockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AutoLogSettingActivity.this,TimeClockActivity.class));
                finish();
            }
        });

        settingIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(AutoLogSettingActivity.this,AutoLogSettingActivity.class));
//                finish();
            }
        });

        clockIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AutoLogSettingActivity.this,RegistrationListActivity.class));
            }
        });

        callIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AutoLogSettingActivity.this,Home.class));
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AutoLogSettingActivity.this,RegistrationTimeActivity.class));
                finish();
            }
        });

        logOutIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("OtherSettings", 0).edit().clear().apply();
                finish();
                startActivity(new Intent(AutoLogSettingActivity.this,Login.class));
            }
        });
    }

    ArrayList<Project> filterProjects(String selectedProjectName) {
        ArrayList<Project> list = new ArrayList<>();
        for(int i = 0; i < projectObList.size(); i ++) {
            Project project = projectObList.get(i);
            if(project.name.equals(selectedProjectName)) {
                list.add(project);
            }
        }
        return list;
    }

    void setUpSetLocationButton() {
        activatedLocationLb.setText(autoTimeTrack.workLocation);
    }

    void setUpTimes() {
        if(autoTimeTrack.isFirstTime) {
            startEt.setText("");
            endEt.setText("");
        } else {
            mStartTime.setTime(autoTimeTrack.startTime);
            updateTime(1);
            mEndTime.setTime(autoTimeTrack.endTime);
            updateTime(2);
        }
    }

    void setUpWeeks() {
        setUpWeekBtn(mBtn, 0);
        setUpWeekBtn(tueBtn, 1);
        setUpWeekBtn(wesBtn, 2);
        setUpWeekBtn(thuBtn, 3);
        setUpWeekBtn(friBtn, 4);
        setUpWeekBtn(satBtn, 5);
        setUpWeekBtn(sunBtn, 6);
    }

    void setUpWeekBtn(TextView btn, int index) {
        if(workdays[index]) {
            btn.setTextColor(getResources().getColor(R.color.colorWhite));
            btn.setBackground(getResources().getDrawable(R.drawable.ic_sel_workday));
        } else {
            btn.setTextColor(getResources().getColor(R.color.colorPrimary));
            btn.setBackground(getResources().getDrawable(R.drawable.ic_unsel_workday));
        }
    }

    void loadProject() {
        constants.showProgress("Please wait...");
        StringRequest request = new StringRequest(
                Request.Method.GET,
                preferences.getString("URL","")+"/api/services/app/project/GetProjectsByUserId?userId=" + preferences.getString("userID", ""),
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
                                obj = obj.getJSONObject("result");
                                JSONArray items=null;
                                items = obj.getJSONArray("items");
                                if(items.length() > 0) {
                                    for(int i = 0; i < items.length(); i ++) {
                                        JSONObject itemObj = items.getJSONObject(i);
                                        boolean isActive = itemObj.getBoolean("isActive");
                                        if(isActive) {
                                            Project project = new Project();
                                            project.userId = itemObj.optInt("id");
                                            project.name = itemObj.optString("name");
                                            projectList.add(project.name);
                                            projectObList.add(project);
                                        }
                                    }
                                    ArrayList<String> temp = Functions.sortArray(projectList);
                                    projectList.clear();
                                    projectList.addAll(temp);
                                    projectSpinnerArrayAdapter.notifyDataSetChanged();
                                }
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
                                Toast.makeText(AutoLogSettingActivity.this, res, Toast.LENGTH_LONG).show();
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

    void loadUI() {
        autoTimeTrack.loadObj();

        if(autoTimeTrack.isActive) activateCheckBox.setChecked(true); else activateCheckBox.setChecked(false);
        if(autoTimeTrack.isSetStartPage) startPageCheckBox.setChecked(true); else startPageCheckBox.setChecked(false);
        if(autoTimeTrack.isCallLogger) callLogCheckBox.setChecked(true); else callLogCheckBox.setChecked(false);
        if(autoTimeTrack.isLocation) locationCheckBox.setChecked(true); else locationCheckBox.setChecked(false);

        System.arraycopy(autoTimeTrack.workdays, 0, workdays, 0, 7);

        projectEt.setText(autoTimeTrack.projectName);
    }

    void findViewByIds() {

        confirmBtn = findViewById(R.id.confirmBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmClicked();
            }
        });

        callLogCheckBox = findViewById(R.id.callLogCheckBox);
        activateCheckBox = findViewById(R.id.activateCheckBox);
        startPageCheckBox = findViewById(R.id.startPageCheckBox);
        locationCheckBox = findViewById(R.id.locationCheckBox);

        mBtn = findViewById(R.id.mBtn);
        tueBtn = findViewById(R.id.tueBtn);
        wesBtn = findViewById(R.id.wesBtn);
        thuBtn = findViewById(R.id.thuBtn);
        friBtn = findViewById(R.id.friBtn);
        satBtn = findViewById(R.id.satBtn);
        sunBtn = findViewById(R.id.sunBtn);

        activatedLocationLb = findViewById(R.id.activatedLocationLb);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workdays[0] = !workdays[0];
                setUpWeekBtn(mBtn, 0);
            }
        });

        tueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workdays[1] = !workdays[1];
                setUpWeekBtn(tueBtn, 1);
            }
        });

        wesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workdays[2] = !workdays[2];
                setUpWeekBtn(wesBtn, 2);
            }
        });

        thuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workdays[3] = !workdays[3];
                setUpWeekBtn(thuBtn, 3);
            }
        });

        friBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workdays[4] = !workdays[4];
                setUpWeekBtn(friBtn, 4);
            }
        });

        satBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workdays[5] = !workdays[5];
                setUpWeekBtn(satBtn, 5);
            }
        });

        sunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workdays[6] = !workdays[6];
                setUpWeekBtn(sunBtn, 6);
            }
        });

    }

    void setSpins() {
        projectEt = findViewById(R.id.projectEt);
        productSpin = findViewById(R.id.productSpin);
        projectView = findViewById(R.id.projectView);

        projectSpinnerArrayAdapter = new MySpinnerAdapter(
                this,R.layout.custom_spin_item, projectList);
        productSpin.setAdapter(projectSpinnerArrayAdapter);

        try {
            Field popup = Spinner.class.getDeclaredField(RegistrationTimeActivity.MPOPUP);
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(productSpin);
            popupWindow.setWidth(projectView.getWidth());
            popupWindow.setHeight(500);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException ignored) { }

        productSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (fistTime) {
                    fistTime = false;
                    return;
                }

                String selectedItem = parent.getItemAtPosition(position).toString();
                projectEt.setText(selectedItem);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        projectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productSpin.performClick();
            }
        });

        projectEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productSpin.performClick();
            }
        });

    }

    void setTimeListener() {
        startEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int hour = mStartTime.get(Calendar.HOUR_OF_DAY);
                int minute = mStartTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AutoLogSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                mTimePicker = new TimePickerDialog(AutoLogSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    @Override public void onLocationChanged(Location location) {
//        onlyOneLocation = location;
        Log.d(TAG, "location -> " + location);

        CurrentLocationManager.lat = (float) location.getLatitude();
        CurrentLocationManager.lng = (float) location.getLongitude();
//        CurrentLocationManager.address = "aaa aaa";



//        locationManager.removeUpdates(this);
    }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override public void onProviderEnabled(String provider) { }
    @Override public void onProviderDisabled(String provider) { }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("gps", "Location permission granted");
                startLocationUpdate();
            }
        }
    }

    private void locationDetect() {

        if(locationUpdatingNow) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        else {
            startLocationUpdate();
        }
    }

    private void startLocationUpdate() {
//        try {
//            locationUpdatingNow = true;
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            assert locationManager != null;
//            locationManager.requestLocationUpdates("gps", 0, 0, this);
//        } catch (SecurityException ex) {
//            locationUpdatingNow = false;
//            Log.d("gps", "Location permission did not work!");
//        }
    }

    public void onClickQuestionLoggerBtn(View view) {
        AlertUtils.showAlert(this, "Call logger will registrate time spent on phone calls, within selected start end work period.");
    }

    public void onClickQuestionCheck(View view) {
        AlertUtils.showAlert(this, "You will receive a notification if you have not checked-in or out within the selected typical work period.");
    }

    public void onClickQuestionSetTimer(View view) {
        AlertUtils.showAlert(this, "If the timer is selected as a start page then you will see check-in and out when you open Portlr APP as the default page.");
    }

    public void onClickQuestionWorkDay(View view) {
        AlertUtils.showAlert(this, "Selected workdays, default project and start / end time is used for when the app should track calls if that is enabled + when notifications will be shown. The default project is the project that check-in and out will be logged on.");
    }

    public void onClickQuestionLocation(View view) {
        AlertUtils.showAlert(this, "Check out based on location shows a notification if you leave your work location and have not checked out");
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();
    }


}
