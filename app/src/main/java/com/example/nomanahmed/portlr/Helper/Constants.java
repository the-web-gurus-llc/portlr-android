package com.example.nomanahmed.portlr.Helper;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * Created by Noman Ahmed on 4/9/2019.
 */
public class Constants {
    public static  int DBVERSION=6;
    SharedPreferences preferences;
    Context context;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    AlertDialog.Builder dialog;
    public Constants(Context context) {
        this.context=context;
        preferences=context.getSharedPreferences("MySettings",Context.MODE_PRIVATE);
        editor=preferences.edit();
        progressDialog=new ProgressDialog(context);
        dialog=new AlertDialog.Builder(context);
    }
    public void addValue(String key, String value) {
        editor.putString(key,value);
        editor.commit();
        editor.apply();
    }
    public void showDialog(String heading,String message)
    {
        dialog.setTitle(heading);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }
    public String getValue(String key)
    {
      return   preferences.getString(key,"");
    }
    public void remove()
    {
        context.getSharedPreferences("MySettings", 0).edit().clear().commit();
    }
    public void showProgress(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }
    public void endProgress() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    public static boolean hasInternetConnectivity(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }
}
