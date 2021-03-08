package com.example.nomanahmed.portlr.Utils;

import android.app.AlertDialog;
import android.content.Context;

public class AlertUtils {

    public static void showAlert(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}
