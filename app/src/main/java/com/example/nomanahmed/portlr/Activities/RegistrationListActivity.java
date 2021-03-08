package com.example.nomanahmed.portlr.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.example.nomanahmed.portlr.Adapters.TimeListItemCustomListAdapter;
import com.example.nomanahmed.portlr.Helper.AppController;
import com.example.nomanahmed.portlr.LocalDatabase.NewConstants;
import com.example.nomanahmed.portlr.Model.TimeLog;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistrationListActivity extends AppCompatActivity {

    final String TAG = NewConstants.APPLICATIONTAG + "RegistrationListActivity";

    TextView subLb;
    ImageView prevBtn;
    ImageView nextBtn;
    EditText dateEt;

    SwipeRefreshLayout pullToRefresh;
    ListView listView;

    Calendar myCalendar = Calendar.getInstance();
    TimeListItemCustomListAdapter adapter;
    ArrayList<TimeLog> filteredSheetList = new ArrayList<>();

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_list);

        preferences=getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);

        subLb = findViewById(R.id.sumLb);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
        dateEt = findViewById(R.id.myDateEt);

        pullToRefresh=(SwipeRefreshLayout)findViewById(R.id.pultorefresh);
        pullToRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });
        listView = (ListView) findViewById(R.id.listview);
        adapter = new TimeListItemCustomListAdapter(this, filteredSheetList);
        listView.setAdapter(adapter);

        showDatePicker();
        setBtns();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDate();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };

    void showDatePicker() {
        dateEt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegistrationListActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    void setBtns() {
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCalendar.add(Calendar.DATE, -1);
                updateDate();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCalendar.add(Calendar.DATE, 1);
                updateDate();
            }
        });
    }

    private void updateDate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEt.setText(sdf.format(myCalendar.getTime()));
        searchData();
    }

    private void Refresh() {
        updateDate();
        pullToRefresh.setRefreshing(false);
    }

    void searchData() {
        filteredSheetList.clear();
        adapter.notifyDataSetChanged();

        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        loadData(sdf.format(myCalendar.getTime()));

    }

    void loadData(String date) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                preferences.getString("URL","")+"/api/services/app/timesheet/GetTimesheetByUserId?UserId=" + preferences.getString("userID", "") + "&Date=" + date,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onResponse:", response);

                        JSONObject obj;
                        try
                        {
                            obj = new JSONObject(response);
                            boolean success = obj.getBoolean("success");
                            if(success) {
                                JSONArray items=null;
                                items = obj.getJSONArray("result");
                                if(items.length() > 0) {
                                    for(int i = 0; i < items.length(); i ++) {
                                        JSONObject itemObj = items.getJSONObject(i);
                                        TimeLog timeLog = new TimeLog();
                                        timeLog.timesheetId = itemObj.optInt("timesheetId");
                                        timeLog.userId = itemObj.optInt("userId");
                                        timeLog.taskId = itemObj.optInt("taskId");
                                        timeLog.taskName = itemObj.optString("taskName");
                                        if(timeLog.taskName.equals("null")) timeLog.taskName = "";
                                        timeLog.projectId = itemObj.optInt("projectId");
                                        timeLog.projectName = itemObj.optString("projectName");
                                        timeLog.spentAt = itemObj.optString("spentAt");
                                        timeLog.startAt = itemObj.optString("startedAt");
                                        timeLog.endAt = itemObj.optString("endedAt");
                                        timeLog.hours = itemObj.optDouble("hours");
                                        timeLog.comment = itemObj.optString("notes");
                                        if(timeLog.comment.equals("null")) timeLog.comment = "";
                                        timeLog.creationTime = DateTimeUtils.ServerToLocalDate(itemObj.getString("creationTime"));
                                        filteredSheetList.add(timeLog);
                                    }
                                    reloadDataAndUI();
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

                        // As of f605da3 the following should work
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                System.out.print(res);
                                Toast.makeText(RegistrationListActivity.this, res, Toast.LENGTH_LONG).show();
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

    void reloadDataAndUI() {
        String sum = "00:00";
        for(int i = 0 ; i < filteredSheetList.size(); i ++) {
            sum = DateTimeUtils.sumServerTime(sum, filteredSheetList.get(i).hours);
        }
        subLb.setText(sum);
        adapter.notifyDataSetChanged();
    }

}
