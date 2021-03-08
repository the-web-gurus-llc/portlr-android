package com.example.nomanahmed.portlr.Helper;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import java.util.Date;
public class Background_Call_Det_Service extends Service {
    //Variables
    Constants constants;
    private broadcasting callclassobject;
    private IntentFilter intentfilter;
    private long callEndTime,callStartTime;
    public static String incomingPhoneNumber="";
    ///////////////////////////////////////////////////////
    //Broadcast receiver inside service to receive calls
    //Broadcast receiver inner class
    class broadcasting extends BroadcastReceiver {
        private final String TAG = this.getClass().getSimpleName();
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcasting", "onReceive: ");
            try {
                ///////////////////////////////////////////////
                TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                telephony.listen(new PhoneStateListener(){
                    @Override
                    public void onCallStateChanged(int state, String incomingNumber) {
                        super.onCallStateChanged(state, incomingNumber);
                        Log.d(incomingNumber, "Number");
                        incomingPhoneNumber=incomingNumber;
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE);
                /////////////////////////////////////////////////
                System.out.println("Receiver start");
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                //    Toast.makeText(context,"Ringing State Number is -"+incomingNumber,Toast.LENGTH_SHORT).show();
                    Log.d("1", "IncomingCallRinging: ");
                }
                if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                    Log.d("2", "CallReceived: ");
                    callStartTime=new Date().getTime();
                }
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    //Toast.makeText(context,"Call Idle State",Toast.LENGTH_SHORT).show();
                    Log.d("3", "CallEndedorCanceled");
                    callEndTime = new Date().getTime();
                    long diff = callEndTime - callStartTime;
                    long minutes = (diff / 1000) / 60;
                    long seconds = (diff / 1000) % 60;
                    Log.d("Time:", minutes+"m "+seconds+"s");
                    //Log.d(isMyContact(context,incomingPhoneNumber)+"", "isMyContct");
                    //Log.d(incomingNumber, "Number: ");
                    Log.d(getContactDisplayNameByNumber(incomingPhoneNumber,context), "Result");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //Function to check the number registration status on phonebook
    public String getContactDisplayNameByNumber(String number,Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "Incoming call from";
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, null, null, null, null);
        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                // this.id =
                // contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                // String contactId =
                // contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }else{
                name = "Unknown number";
            }
        }catch (Exception ex)
        {
            Log.d("*Error*", "getContactDisplayNameByNumber: ");
        }
        finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }
        return name;
    }
    private boolean isMyContact(Context context,String number) {
        boolean isFound=false;
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor c = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (c != null)
            { // cursor not null means number is found contactsTable
                isFound=true;
                Log.d(c.getCount()+"", "CursorLength");
                if (c.moveToNext())
                {   // so now find the contact Name
                    String res = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Log.d(res, "ContactName: ");
                }
                c.close();
            }
        } catch (Exception ex) {
        /* Ignore */
            Log.d("error", "isMyContact: ");
        }
        return isFound;
    }
    //Broadcast receiver class ends
    ///////////////////////////////////////////////////////////
    public Background_Call_Det_Service() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("called", "onCreate: ");
        constants=new Constants(getApplicationContext());
        callclassobject =new broadcasting();
        intentfilter =new IntentFilter();
        intentfilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(callclassobject, intentfilter);
        Log.d("Intenet reg success", "onCreate");
    }
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        constants.addValue("SERVICE_STATUS","ON");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callclassobject!=null)
        {
          try{
              unregisterReceiver(callclassobject);
          }  catch (Exception ex)
          {

              Log.d("Error", "UnregistringReceiver ");
          }
        }
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        startService(new Intent(getApplicationContext(),Background_Call_Det_Service.class));
    }
    /*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }*/
}