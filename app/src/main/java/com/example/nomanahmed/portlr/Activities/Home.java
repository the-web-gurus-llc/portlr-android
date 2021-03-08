package com.example.nomanahmed.portlr.Activities;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nomanahmed.portlr.Fragments.Registration;
import com.example.nomanahmed.portlr.Fragments.Settings;
import com.example.nomanahmed.portlr.R;

import java.util.ArrayList;
import java.util.List;
public class Home extends AppCompatActivity {
//    public  static DrawerLayout mDrawerLayout;
//    NavigationView mNavigationView;
    FragmentTransaction transaction;
    FragmentManager manager;
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    String[] permissions= new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.PROCESS_OUTGOING_CALLS};
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preferences=getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);
        editor=preferences.edit();
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
//        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        // mNavigationView.setItemTextColor(ColorStateList.valueOf(Color.BLACK));
//        View header = mNavigationView.getHeaderView(0);
//        TextView txt1,txt2;
//        txt1=(TextView)header.findViewById(R.id.t1);
//        txt2=(TextView)header.findViewById(R.id.t2);
//        Typeface face = Typeface.createFromAsset(getAssets(),
//                "fonts/concertone-regular.ttf");
//        txt1.setTypeface(face);
//        face = Typeface.createFromAsset(getAssets(),
//                "fonts/Roboto-Bold.ttf");
//        txt2.setTypeface(face);



        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.containerView, new Registration()).commit();
//        checkPermissions();
    }
    @Override
    public void onBackPressed() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        if (preferences.getString("Screen","").equals("Registrations"))
        {
            if (Registration.searchtxt.getText().toString().length()>0) {
                editor.putString("RecentDate", Registration.searchtxt.getText().toString());
                editor.commit();
                editor.apply();
            }
            finish();
        }else  if (preferences.getString("Screen","").equals("ClientDetails"))
        {
            transaction.replace(R.id.containerView,new Registration()).commit();
        }else  if (preferences.getString("Screen","").equals("Settings"))
        {
            transaction.replace(R.id.containerView,new Registration()).commit();
        }
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
}
