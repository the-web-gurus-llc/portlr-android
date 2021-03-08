package com.example.nomanahmed.portlr.Fragments;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.example.nomanahmed.portlr.Activities.Home;
import com.example.nomanahmed.portlr.Helper.AppController;
import com.example.nomanahmed.portlr.Helper.Constants;
import com.example.nomanahmed.portlr.LocalDatabase.OfflineDB;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Adapters.Registrations_Adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
/**
 * Created by Noman Ahmed on 4/26/2019.
 */
public class Registration extends Fragment {
    ListView listView;
    Registrations_Adapter adapter;
    TextView NoReg;
    SwipeRefreshLayout pullToRefresh;
    OfflineDB db;
//    ImageView menu;
    Constants constants;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    FragmentTransaction transaction;
    FragmentManager manager;
    public static EditText searchtxt;
//    ImageView calendarimg;

    ImageView prevBtn;
    ImageView nextBtn;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_registrations, null, false);
        constants=new Constants(getActivity());
        preferences=getActivity().getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);
        editor=preferences.edit();
        editor.putString("Screen","Registrations");
        editor.commit();
        editor.apply();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        searchtxt=(EditText)view.findViewById(R.id.searchdate);
        searchtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = searchtxt.getText().toString().toLowerCase();
                if (adapter != null) {
                    Log.d(text, "onTextChanged: ");
                        adapter.getFilter().filter(text);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (Registration.searchtxt.getText().toString().length() > 0) {
                    editor.putString("RecentDate", Registration.searchtxt.getText().toString());
                    editor.commit();
                    editor.apply();
                }
            }
        });
//        calendarimg=(ImageView)view.findViewById(R.id.calendarimg);
        searchtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferences.getString("RecentDate", "").length() > 0)
                {
                    String[] date=preferences.getString("RecentDate","").split("-");
                    day=Integer.parseInt(date[0]);
                    month=Integer.parseInt(date[1]);
                    month--;
                    year=Integer.parseInt(date[2]);
                    Log.d("recent date", "onCreateView: ");
                }
                DatePickerDialog dialog=new DatePickerDialog(getActivity(),myDateListener, year, month, day);
                dialog.show();
            }
        });
        NoReg=(TextView)view.findViewById(R.id.noreg);
//          menu=(ImageView)view.findViewById(R.id.menu);
//        menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Home.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                Home.mDrawerLayout.openDrawer(Gravity.LEFT);
//                Home.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//            }
//        });
        listView = (ListView) view.findViewById(R.id.listview);
        pullToRefresh=(SwipeRefreshLayout)view.findViewById(R.id.pultorefresh);
        pullToRefresh.setColorSchemeColors(getResources().getColor(R.color.topcolor));
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });
        Refresh();
//        TextView txt1,txt2;
//        txt1=(TextView)view.findViewById(R.id.t1);
//        txt2=(TextView)view.findViewById(R.id.t2);
//        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
//                "fonts/concertone-regular.ttf");
//        txt1.setTypeface(face);
//        face = Typeface.createFromAsset(getActivity().getAssets(),
//                "fonts/Roboto-Bold.ttf");
//        txt2.setTypeface(face);

        prevBtn = view.findViewById(R.id.prevBtn);
        nextBtn = view.findViewById(R.id.nextBtn);

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferences.getString("RecentDate", "").length() > 0)
                {
                    String[] date=preferences.getString("RecentDate","").split("-");
                    day=Integer.parseInt(date[0]);
                    month=Integer.parseInt(date[1]);
                    month--;
                    year=Integer.parseInt(date[2]);
                    Log.d("recent date", "onCreateView: ");
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.add(Calendar.DATE, -1);
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                searchtxt.setText(sdf.format(calendar.getTime()));
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferences.getString("RecentDate", "").length() > 0)
                {
                    String[] date=preferences.getString("RecentDate","").split("-");
                    day=Integer.parseInt(date[0]);
                    month=Integer.parseInt(date[1]);
                    month--;
                    year=Integer.parseInt(date[2]);
                    Log.d("recent date", "onCreateView: ");
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.add(Calendar.DATE, 1);
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                searchtxt.setText(sdf.format(calendar.getTime()));
            }
        });

        return view;
    }
    public void Refresh() {
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();
        db = new OfflineDB(getActivity(), null, null, Constants.DBVERSION);
        if (db.getdata().size() > 0) {
            NoReg.setVisibility(View.INVISIBLE);
            adapter = new Registrations_Adapter(getActivity(), db.getdata(), transaction, this);
            listView.setAdapter(adapter);
        } else {
            NoReg.setVisibility(View.VISIBLE);
        }
        pullToRefresh.setRefreshing(false);

        if (preferences.getString("RecentDate", "").length() > 0)
        {
            if (preferences.getString("TodayDate","").equals("yes"))
            {
                editor=preferences.edit();
                editor.putString("TodayDate","no");
                editor.apply();
                editor.commit();
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String dateStart = df.format(Calendar.getInstance().getTime());
                searchtxt.setText(dateStart.split(" ")[0].replace("/", "-"));
            }else {
                searchtxt.setText(preferences.getString("RecentDate", ""));
            }
        } else {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dateStart = df.format(Calendar.getInstance().getTime());
            searchtxt.setText(dateStart.split(" ")[0].replace("/", "-"));
        }
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker arg0,int arg1, int arg2, int arg3)
                {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                   // showDate(arg1, arg2+1, arg3);
                    arg2++;
                    String month=arg2+"";
                    if (arg2<10)
                    {
                        month="0"+month;
                    }
                    String day=arg3+"";
                    if (arg3<10)
                    {
                        day="0"+day;
                    }
                    searchtxt.setText(day+"-"+month+"-"+arg1);
                    Log.d(day+"-"+month+"-"+arg1, "onDateSet: ");
                }
            };
}
