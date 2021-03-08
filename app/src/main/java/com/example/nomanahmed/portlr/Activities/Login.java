package com.example.nomanahmed.portlr.Activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.nomanahmed.portlr.Helper.AppController;
import com.example.nomanahmed.portlr.Helper.Constants;
import com.example.nomanahmed.portlr.Model.AutoTimeTrack;
import com.example.nomanahmed.portlr.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

public class Login extends AppCompatActivity {
    EditText url , email , pass;
    Button login;
    Constants constants;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String urlStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearDB();

        constants=new Constants(this);
        preferences=getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);
        editor=preferences.edit();
        url=(EditText)findViewById(R.id.url);
        email=(EditText)findViewById(R.id.email);
        pass=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().length()>0&&url.getText().toString().length()>0&&pass.getText().toString().length()>0)
                {
                    login();
                }else
                {
                    Toast.makeText(Login.this, "Please enter above information", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void clearDB() {
        AutoTimeTrack autoTimeTrack = new AutoTimeTrack(getApplicationContext());
        autoTimeTrack.clearObj();
        autoTimeTrack.removeAllNotifications();
    }

    private void getAccessToken()
    {
        constants.showProgress("Validating user...");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                urlStr+"/api/Account/Authenticate",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onResponse:", response);
                        constants.endProgress();
                        try
                        {
                            JSONObject object=new JSONObject(response);
                            if (object.getBoolean("success"))
                            {
                                    editor.putString("token",object.getString("result"));
                                    editor.putString("isLogin","true");
                                    editor.apply();
                                    editor.commit();
                                    Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                                    finish();
//                                    startActivity(new Intent(Login.this,Home.class));
                                startActivity(new Intent(Login.this,RegistrationTimeActivity.class));
                            }
                        }catch (Exception ex)
                        {
                            Log.d("error", "onResponse: ");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                // Now you can use any deserializer to make sense of data
                                //JSONObject obj = new JSONObject(res);
                                Log.d(res, "onErrorResponse: ");
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (Exception e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody()
            {
                JSONObject params= new JSONObject();
                String body = null;
                try
                {
                    params.put("apiKey", preferences.getString("apiKey",""));
                    params.put("apiSecret", preferences.getString("apiSecret",""));
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
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request, "srarequest");

    }
    private void login()
    {

        urlStr = url.getText().toString();
        String HTTPSHEADER = "https://";
        if(!urlStr.contains(HTTPSHEADER)) {
            urlStr = HTTPSHEADER + urlStr;
        }

        constants.showProgress("Validating user...");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                urlStr+"/api/Authorize/IsValidUser",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onResponse:", response);
                        constants.endProgress();
                        try
                        {
                            JSONObject object=new JSONObject(response);
                            if (object.getBoolean("success"))
                            {
                                    object=object.getJSONObject("result");
                                    editor.putString("apiKey",object.getString("apiKey"));
                                    editor.putString("apiSecret",object.getString("apiSecret"));
                                    editor.putString("userID",object.getInt("userId")+"");
                                editor.putString("URL",urlStr);
                                editor.apply();
                                    editor.commit();
                                    getAccessToken();
                            }else
                            {
                                constants.showDialog("Error","Error authenticating user");
                            }
                        }catch (Exception ex)
                        {
                            Log.d("error", "onResponse: ");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                // Now you can use any deserializer to make sense of data
                                //JSONObject obj = new JSONObject(res);
                                Log.d(res, "onErrorResponse: ");
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (Exception e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody()
            {
                JSONObject params= new JSONObject();
                String body = null;
                try
                {
                    params.put("emailAddress", email.getText().toString());
                    params.put("password", pass.getText().toString());
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
        };
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.d("timeout", "retry: ");
            }
        });
        AppController.getInstance().addToRequestQueue(request, "srarequest");
    }
}
