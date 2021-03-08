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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.nomanahmed.portlr.Helper.Constants;
import com.example.nomanahmed.portlr.LocalDatabase.NewConstants;
import com.example.nomanahmed.portlr.Model.AutoTimeTrack;
import com.example.nomanahmed.portlr.Model.Project;
import com.example.nomanahmed.portlr.Model.Task;
import com.example.nomanahmed.portlr.Model.TimeLog;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.AlertUtils;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;
import com.example.nomanahmed.portlr.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_TIMELOG;
import static com.example.nomanahmed.portlr.Utils.Global.selected_time_log;

public class RegistrationTimeActivity extends AppCompatActivity {

    final String TAG = NewConstants.APPLICATIONTAG + "RegistrationTimeActivity";
    public final static String MPOPUP = "mPopup";

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
//    String[] permissions= new String[]{
//            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.READ_PHONE_NUMBERS,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.READ_CALL_LOG,
//            Manifest.permission.PROCESS_OUTGOING_CALLS};

    String[] permissions= new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS};

    EditText startEt;
    EditText endEt;
    EditText resultEt;
    EditText commentEt;
    Spinner productSpin;
    Spinner taskSpin;

    EditText projectEt;
    EditText taskEt;
    View projectView;
    View taskView;

    ImageView settingIV;
    ImageView clockIV;
    ImageView logOutIV;
    ImageView callIV;
    ImageView timeClockBtn;
    ImageView questionBtn;
    Button doneBtn;
    Button cancelBtn;

    View bottomView;

    Calendar mStartTime = Calendar.getInstance();
    Calendar mEndTime = Calendar.getInstance();

    String SELECT_PROJECT = "Project";
    String SELECT_TASK = "Task";

    List<String> projectList = new ArrayList<>();
    MySpinnerAdapter projectSpinnerArrayAdapter;

    List<String> taskList = new ArrayList<>();
    MySpinnerAdapter taskSpinnerArrayAdapter;

    List<Project> projectObList = new ArrayList<>();
    List<Task> taskObList = new ArrayList<>();

    TimeLog selectedLog;
    String resultStr = "";

    Constants constants;
    SharedPreferences preferences;

    boolean fistTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_time);

        checkPermissions();

        constants=new Constants(this);
        preferences=getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);

        selectedLog = selected_time_log;

        startEt = findViewById(R.id.startEt);
        endEt = findViewById(R.id.endEt);
        resultEt = findViewById(R.id.resultEt);
        commentEt = findViewById(R.id.commentEt);

        setBtns();
        setBottomBtns();
        setTimeListener();
        setSpins();

        clearUI();
        loadLog();
        loadProject();
    }

    void setBtns() {
        doneBtn = findViewById(R.id.doneBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        bottomView = findViewById(R.id.bottomView);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAdd();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedLog != null) {
                    dismiss();
                } else {
                    clearUI();
                }
            }
        });
    }

    void dismiss() {
        finish();
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
                                Toast.makeText(RegistrationTimeActivity.this, res, Toast.LENGTH_LONG).show();
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

    void loadTasks(String projectId) {

        String userId = preferences.getString("userID", "");

        constants.showProgress("Please wait...");
        StringRequest request = new StringRequest(
                Request.Method.GET,
                preferences.getString("URL","")+"/api/services/app/task/GetProjectsTasks?userId=" + userId + "&ProjectId=" + projectId,
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
                                        boolean isActive = itemObj.optBoolean("isClosed");
                                        if(!isActive) {
                                            Task task = new Task();
                                            task.id = itemObj.optInt("id");
                                            task.title = itemObj.optString("title");
                                            taskList.add(task.title);
                                            taskObList.add(task);
                                        }
                                    }
                                    ArrayList<String> temp = Functions.sortArray(taskList);
                                    taskList.clear();
                                    taskList.addAll(temp);
                                    taskSpinnerArrayAdapter.notifyDataSetChanged();
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
                                Toast.makeText(RegistrationTimeActivity.this, res, Toast.LENGTH_LONG).show();
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

    void updateTaskList(int projectId) {

        taskList.clear();
        taskObList.clear();
        taskSpinnerArrayAdapter.notifyDataSetChanged();
        taskEt.setText(SELECT_TASK);

        String selectedProject = projectEt.getText().toString();
        if(selectedProject.equals(SELECT_PROJECT)) {
            return;
        }

        if (projectId != NewConstants.DEFAULT_TASK_ID) {
            loadTasks(String.valueOf(projectId));
            return;
        }

        ArrayList<Project> selectedProjects = filterProjects(selectedProject);
        if(selectedProjects.size() != 0) {
            int selectedId = selectedProjects.get(0).userId;
            loadTasks(String.valueOf(selectedId));
        }

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

    ArrayList<Task> filterTasks(String selectedTaskName) {
        ArrayList<Task> list = new ArrayList<>();
        for(int i = 0; i < taskObList.size(); i ++) {
            Task task = taskObList.get(i);
            if(task.title.equals(selectedTaskName)) {
                list.add(task);
            }
        }
        return list;
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

        long interval = time2.getTime() - time1.getTime();
        interval = interval / 1000;

        if(interval <= 0) {
            AlertUtils.showAlert(this, "End Time should be greater than Start Time");
            return;
        }

        long hour = (interval / 3600);
        long minute = (interval % 3600) / 60;

        resultEt.setText(hour + ":" + minute);
    }

    String[] getHoursDetail() {
        int intervalInt = DateTimeUtils.getIntervalTime(resultStr);
        if(intervalInt == -1) {
            return new String[0];
        }

        double interval = (double) intervalInt;
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

    void loadLog() {
        if (selectedLog == null) {
            return;
        }

        doneBtn.setText(R.string.update);
        bottomView.setVisibility(View.GONE);

        commentEt.setText(this.selectedLog.comment);
        if(!selectedLog.startAt.equals("")) {
            mStartTime.setTime(DateTimeUtils.getStringToTime(selectedLog.startAt));
            updateTime(1);
        }

        if(!selectedLog.endAt.equals("")) {
            mEndTime.setTime(DateTimeUtils.getStringToTime(selectedLog.endAt));
            updateTime(2);
        }

        if(!selectedLog.hours.equals(NewConstants.DEFAULT_HOUR)) {
            resultEt.setText(DateTimeUtils.getLocalTimeFromServer(selectedLog.hours));
        }

        if(selectedLog.projectId == NewConstants.DEFAULT_PROJECT_ID) {
            return;
        }

        projectEt.setText(selectedLog.projectName);
        updateTaskList(selectedLog.projectId);

        if(selectedLog.taskId == NewConstants.DEFAULT_TASK_ID) {
            return;
        }

        taskEt.setText(selectedLog.taskName);
    }

    void clearUI() {
        mStartTime.set(Calendar.MILLISECOND, 0);
        mStartTime.set(Calendar.SECOND, 0);
        mStartTime.set(Calendar.MINUTE, 0);
        mStartTime.set(Calendar.HOUR, 9);

        mEndTime.set(Calendar.MILLISECOND, 0);
        mEndTime.set(Calendar.SECOND, 0);
        mEndTime.set(Calendar.MINUTE, 0);
        mEndTime.set(Calendar.HOUR, 9);

        startEt.setText("");
        endEt.setText("");
        commentEt.setText("");
        resultEt.setText("");
        projectEt.setText(SELECT_PROJECT);
        taskEt.setText(SELECT_TASK);

    }

    void setSpins() {
        projectEt = findViewById(R.id.projectEt);
        productSpin = findViewById(R.id.productSpin);
        projectView = findViewById(R.id.projectView);

        taskSpin = findViewById(R.id.taskSpin);
        taskEt = findViewById(R.id.taskEt);
        taskView = findViewById(R.id.taskView);

        projectSpinnerArrayAdapter = new MySpinnerAdapter(
                this,R.layout.custom_spin_item, projectList);
        taskSpinnerArrayAdapter =  new MySpinnerAdapter(
                this,R.layout.custom_spin_item, taskList);
        productSpin.setAdapter(projectSpinnerArrayAdapter);
        taskSpin.setAdapter(taskSpinnerArrayAdapter);

        try {
            Field popup = Spinner.class.getDeclaredField(MPOPUP);
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(productSpin);
            popupWindow.setWidth(projectView.getWidth());
            popupWindow.setHeight(500);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException ignored) {

        }

        try {
            Field popup = Spinner.class.getDeclaredField(MPOPUP);
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(taskSpin);
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(500);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException ignored) {

        }

        productSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                if(fistTime) {
                    fistTime = false;
                    return;
                }

                String selectedItem = parent.getItemAtPosition(position).toString();
                projectEt.setText(selectedItem);
                updateTaskList(NewConstants.DEFAULT_PROJECT_ID);
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        taskSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                String selectedItem = parent.getItemAtPosition(position).toString();
                taskEt.setText(selectedItem);
            }
            public void onNothingSelected(AdapterView<?> parent) { }
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

        taskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskSpin.performClick();
            }
        });

        taskEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskSpin.performClick();
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
                mTimePicker = new TimePickerDialog(RegistrationTimeActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                mTimePicker = new TimePickerDialog(RegistrationTimeActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    void setBottomBtns() {
        questionBtn = findViewById(R.id.questionBtn);
        clockIV = findViewById(R.id.clockBtn);
        logOutIV = findViewById(R.id.logoutBtn);
        callIV = findViewById(R.id.callBtn);
        timeClockBtn = findViewById(R.id.timeClockBtn);
        settingIV = findViewById(R.id.settingBtn);

        if (new AutoTimeTrack(this).isCheckCallLogger()) {
            callIV.setVisibility(View.VISIBLE);
        } else callIV.setVisibility(View.GONE);

        settingIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationTimeActivity.this,AutoLogSettingActivity.class));
                finish();
            }
        });

        timeClockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationTimeActivity.this,TimeClockActivity.class));
                finish();
            }
        });

        questionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertUtils.showAlert(RegistrationTimeActivity.this, "You can enter 15 minutes by writing 0015, or one hour just by writing 1");
            }
        });

        clockIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationTimeActivity.this,RegistrationListActivity.class));
            }
        });

        callIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationTimeActivity.this,Home.class));
            }
        });

        logOutIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("OtherSettings", 0).edit().clear().apply();
                finish();
                startActivity(new Intent(RegistrationTimeActivity.this,Login.class));
            }
        });
    }

    void coverResult() {
        if(resultStr.contains(":")) {
            return;
        }

        if(resultStr.contains(".") || resultStr.contains(",") || resultStr.contains(";")) {
            return;
        }

        if(resultStr.length() == 1) {
            resultStr = resultStr + ":00";
            return;
        }
        if(resultStr.length() == 2) {
            resultStr = resultStr.substring(0,1) + ":" + resultStr.substring(1,resultStr.length());
            return;
        }
        if (resultStr.length() == 3) {
            resultStr = resultStr.substring(0,1) + ":" + resultStr.substring(1, resultStr.length());
            return;
        }
        if(resultStr.length() == 4) {
            resultStr = resultStr.substring(0,2) + ":" + resultStr.substring(2, resultStr.length());
        }
    }

    void onAdd() {
        String selectedProjectName = projectEt.getText().toString();
        String selectedTaskName = taskEt.getText().toString();
        resultStr = resultEt.getText().toString();
        coverResult();
        if (selectedProjectName.equals(SELECT_PROJECT)) {
            AlertUtils.showAlert(this, "Please select a project");
            return;
        }
        if(resultStr.equals("")) {
            AlertUtils.showAlert(this, "Please input correct times");
            return;
        }
        ArrayList<Project> selectedProject = filterProjects(selectedProjectName);
        String selectedProjectId = "";
        if(!selectedProject.isEmpty()) {
            selectedProjectId = String.valueOf(selectedProject.get(0).userId);
        }

        ArrayList<Task> selectedTask = filterTasks(selectedTaskName);
        String selectedTaskId = "";
        if(!selectedTask.isEmpty()) {
            selectedTaskId = String.valueOf(selectedTask.get(0).id);
        }

        String spent = DateTimeUtils.changeDateFormat(new Date(), "yyyy-MM-dd") + "T" + DateTimeUtils.changeTimeFormat(mStartTime.getTime()) + ".000Z";
        final String[] arrHoursValues = getHoursDetail();

        if (arrHoursValues.length == 0) {
            AlertUtils.showAlert(this, "Hours type should be hh:mm");
            return;
        }

        String startAt = "09:00:00";
        if(!startEt.getText().toString().isEmpty()) {
            startAt = DateTimeUtils.changeTimeFormatSpe(mStartTime.getTime());
        }

        String endAt = "";
        if(!endEt.getText().toString().isEmpty()) {
            endAt = DateTimeUtils.changeTimeFormatSpe(mEndTime.getTime());
        } else {
            Date startT = DateTimeUtils.getStringToTime("09:00:00");
            int interval = DateTimeUtils.getIntervalTime(resultStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startT);
            calendar.setTimeInMillis(calendar.getTimeInMillis() + interval * 1000);
            startT = calendar.getTime();
            endAt = DateTimeUtils.changeTimeFormatSpe(startT);
        }
        int timeSheetId = 0;
        if (selectedLog != null) {
            timeSheetId = selectedLog.timesheetId;
            if(selectedTaskId.equals("")) {
                selectedTaskId = String.valueOf(selectedLog.taskId);
            }

            if(!selectedLog.spentAt.isEmpty()) {
                spent = selectedLog.spentAt;
            }
        }

        constants.showProgress("Please wait...");
        final int finalTimeSheetId = timeSheetId;
        final String finalSelectedProjectId = selectedProjectId;
        final String finalSelectedTaskId = selectedTaskId;
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
                                if(selectedLog != null) {
                                    new AlertDialog.Builder(RegistrationTimeActivity.this)
                                            .setTitle("")
                                            .setMessage("Your time is updated successfully")
                                            .setCancelable(false)
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dismiss();
                                                }
                                            }).show();
                                } else {
                                    new AlertDialog.Builder(RegistrationTimeActivity.this)
                                            .setTitle("")
                                            .setMessage("Your time is registered successfully")
                                            .setCancelable(false)
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    clearUI();
                                                }
                                            }).show();
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
                                Toast.makeText(RegistrationTimeActivity.this, res, Toast.LENGTH_LONG).show();
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
                    params.put("notes", commentEt.getText().toString());
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
                    Log.d(TAG, "parmas -> " + body.toString());
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

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0)
                {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += "\n" + per;
                        }
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selected_time_log = null;
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();
    }

}
