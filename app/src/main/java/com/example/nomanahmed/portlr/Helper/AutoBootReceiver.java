package com.example.nomanahmed.portlr.Helper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoBootReceiver extends BroadcastReceiver {
    Constants constants;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        constants = new Constants(context);
       // constants.remove();
    }
}
