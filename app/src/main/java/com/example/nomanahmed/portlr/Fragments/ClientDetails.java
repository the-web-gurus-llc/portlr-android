package com.example.nomanahmed.portlr.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.nomanahmed.portlr.Activities.RegistrationTimeActivity;
import com.example.nomanahmed.portlr.Adapters.MySpinnerAdapter;
import com.example.nomanahmed.portlr.DataProviders.OfflineDBProvider;
import com.example.nomanahmed.portlr.Helper.AppController;
import com.example.nomanahmed.portlr.Helper.Constants;
import com.example.nomanahmed.portlr.LocalDatabase.NewConstants;
import com.example.nomanahmed.portlr.LocalDatabase.OfflineDB;
import com.example.nomanahmed.portlr.Model.Project;
import com.example.nomanahmed.portlr.Model.Task;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ClientDetails extends Fragment {
//    ImageView back;
    final String MPOPUP = "mPopup";

    TreeMap<String,Integer> clients,project;
    Constants constants;

    Button registerTime;
    ArrayList<String> clientstemp;
    private String clientid="";

    private String projectID = "";
    private String taskID = "";

    TextView stime, etime, duration;
//    TextView status;

    Button cancelBtn;

    Spinner productSpin;
    Spinner taskSpin;

    EditText projectEt;
    EditText taskEt;

    View projectView;
    View taskView;

    boolean fistTime = true;

    String SELECT_PROJECT = "Project";
    String SELECT_TASK = "Task";

    List<String> projectList = new ArrayList<>();
    MySpinnerAdapter projectSpinnerArrayAdapter;

    List<String> taskList = new ArrayList<>();
    MySpinnerAdapter taskSpinnerArrayAdapter;

    List<Project> projectObList = new ArrayList<>();
    List<Task> taskObList = new ArrayList<>();

   public  static  String ID="",CLIENTNAME="";
   SharedPreferences preferences;
   SharedPreferences.Editor editor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_details, null, false);
        preferences=getActivity().getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);
        editor=preferences.edit();
        editor.putString("Screen","ClientDetails");
        editor.commit();
        editor.apply();
        setSpins(view);
        initUI(view);
        loadProject();

        return view;
    }

    private void previewRegister() {
        String selectedProjectName = projectEt.getText().toString();
        String selectedTaskName = taskEt.getText().toString();

        if (selectedProjectName.equals(SELECT_PROJECT)) {
            AlertUtils.showAlert(getActivity(), "Please select a project");
            return;
        }

        if(selectedTaskName.equals(SELECT_TASK)) selectedTaskName = "";

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

        RegisterHours(selectedProjectId, selectedTaskId, selectedProjectName, selectedTaskName);
    }

    private void RegisterHours(final String selectedProjectId, final String selectedTaskId, final String selectedProjectName, final String selectedTaskName) {
        constants.showProgress("Registering hours...");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                preferences.getString("URL","")+"/api/services/app/timesheet/CreateOrUpdateTimesheet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onResponse:", response);
                        constants.endProgress();
                        try
                        {
                         JSONObject obj=new JSONObject(response);
                         if (obj.getBoolean("success"))
                         {
                             constants.showDialog("Success","Hours Registered Successfully");
                             OfflineDB db=new OfflineDB(getActivity(),null,null,Constants.DBVERSION);
                             db.updateRegistration(ID, selectedProjectId, selectedTaskId, selectedProjectName, selectedTaskName);
                             registerTime.setText("Time Registered");
                         }else
                         {
                             constants.showDialog("Error","Failed to Register hours this time, Try again later");
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
            public Map< String, String > getHeaders() throws AuthFailureError {
                HashMap< String, String > headers = new HashMap < String, String > ();
                headers.put("Content-Type","application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " +preferences.getString("token",""));
                return headers;
            }
            @Override
            public byte[] getBody()
            {
                JSONObject params= new JSONObject();
                String body = null;
                try
                {
                    OfflineDB db=new OfflineDB(getActivity(),null,null,Constants.DBVERSION);
                    OfflineDBProvider d=db.getdataByID(ID);
                    Log.d("Registerng Hours", "getBody:");
                    Log.d(d.getEndtime().split(" ")[1], "EndedAt: ");
//                    Log.d(getHours(d.getDuration()), "Hours: ");
//                    Log.d(getRoundedHours(getHours(d.getDuration())), "roundedHours: ");
                    Log.d(constants.getValue("ProjectID"), "projectID ");
                    Log.d(constants.getValue("UserID"), "userID ");
                    Log.d(d.getEndtime(), "spentAt ");
                    Log.d(d.getStarttime().split(" ")[1], "startedAT: ");

                    params.put("endedAt",d.getEndtime().split(" ")[1]);
                    String[] arrHoursValues = getHoursDetail(d.getDuration());
                    params.put("hours", DateTimeUtils.getServerTimeForPost(arrHoursValues[0]));
                    params.put("roundedHours", DateTimeUtils.getServerTimeForPost(arrHoursValues[1]));
                    params.put("isInvoice", "false");
                    params.put("notes","Phone Conversation");
//                    params.put("projectId", constants.getValue("ProjectID"));
                    params.put("projectId", selectedProjectId);
                    params.put("taskId", selectedTaskId);
                    params.put("spentAt",d.getEndtime());
                    params.put("startedAt", d.getStarttime().split(" ")[1]);
                    params.put("userId", preferences.getString("userID",""));
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
//    private String getRoundedHours(String time) {
//        double t=Double.parseDouble(time);
//        if (t<15)
//        {
//            return "15";
//        }else if (t<30&&t>15)
//        {
//            return "30";
//        }else if (t<45&&t>30)
//        {
//            return "45";
//        }else if (t<60&&t>45)
//        {
//            return "60";
//        }
//        return "0";
//    }
//    private String getHours(String time) {
//        String[] list = time.split(":");
//
//
//
//    }
    private void initUI(View view) {
        constants=new Constants(getActivity());
        cancelBtn = view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager manager=getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction=manager.beginTransaction();
                transaction.replace(R.id.containerView,new Registration()).commit();
            }
        });
        registerTime=(Button)view.findViewById(R.id.registertime);
        registerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.hasInternetConnectivity(getActivity())) {
                    if (registerTime.getText().toString().equals("Register Time"))
                    {
//                        getProjectsByClient();
                        previewRegister();
                    }
                    else
                        constants.showDialog("Info", "Hours already registered");
                }else
                {
                    constants.showDialog("No Internet","Active internet connection is required to perform this action");
                }
            }
        });

        stime=(TextView)view.findViewById(R.id.starttime);
        etime=(TextView)view.findViewById(R.id.endtime);
        duration=(TextView)view.findViewById(R.id.callduration);

        getLocalData();
    }
    /*
    private boolean isUserProject(JSONObject object) {
        try
        {
            JSONArray team=object.getJSONArray("team");
            if (team.length()>0)
            {
             for(int i=0;i<team.length();i++) {
                 object=team.getJSONObject(i);
                 if (preferences.getString("userID","").equals(object.getInt("userId")+""))
                 {
                     return true;
                 }
             }
             return false;
            }
        }catch (Exception ex)
        {
            return false;
        }
        return false;
    }
     */
    /*
    private void showProjects() {
        if (project.size() > 0) {
            String[] data=new String[project.size()] ;
            Set set = project.entrySet();
            Iterator iterator = set.iterator();
            int j=0;
            while(iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry)iterator.next();
                data[j++]=mentry.getKey().toString();
                System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
                System.out.println(mentry.getValue());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Project");
            builder.setItems(data, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Object key = project.keySet().toArray(new Object[project.size()])[which];
                    Object value = project.get(key);
                    constants.addValue("ProjectID",value.toString());
                    RegisterHours();
                }
            });
            builder.show();
        }else
        {
            constants.showDialog("Info","No projects found for this client");
        }
    }
     */
//    private void parseProjectsjson(String json) {
//        try
//        {
//            JSONObject obj=new JSONObject(json);
//            obj=obj.getJSONObject("result");
//            JSONArray items=obj.getJSONArray("items");
//            if (items.length()>0)
//            {
//                project=new TreeMap<>();
//                for(int i=0;i<items.length();i++) {
//                    obj = items.getJSONObject(i);
//                    if (isUserProject(obj)) {
//                        project.put(obj.getString("name"),obj.getInt("id"));
//                    }
//                }
//                showProjects();
//            }else
//            {
//             constants.showDialog("Alert","No projects found for this client");
//            }
//        }catch (Exception ex)
//        {
//            Log.d("error", "parseProjectsjson: ");
//        }
//    }
//    private void getProjectsByClient() {
//        constants.showProgress("Getting Client Projects...");
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme("https")
//                .authority(preferences.getString("URL","").substring(8))
//                .appendPath("api")
//                .appendPath("services")
//                .appendPath("app")
//                .appendPath("project")
//                .appendPath("GetProjectsByClientId")
//                .appendQueryParameter("clientId",clientid);
//        String url=builder.build().toString();
//        Log.d(url, "getProjectsByClient: ");
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("onResponse:", response);
//                        constants.endProgress();
//                        parseProjectsjson(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        constants.endProgress();
//                        // As of f605da3 the following should work
//                        NetworkResponse response = error.networkResponse;
//                        if (error instanceof ServerError && response != null) {
//                            try {
//                                String res = new String(response.data,
//                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//                                // Now you can use any deserializer to make sense of data
//                                //JSONObject obj = new JSONObject(res);
//                                System.out.print(res);
//                                Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();
//                            } catch (UnsupportedEncodingException e1) {
//                                // Couldn't properly decode data to string
//                                Log.d("err4", "onErrorResponse: ");
//                            }
//                        }
//                    }
//                }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//            @Override
//            public Map< String, String > getHeaders() throws AuthFailureError {
//                HashMap< String, String > headers = new HashMap < String, String > ();
//                headers.put("Content-Type","application/json; charset=utf-8");
//                headers.put("Authorization", "Bearer " +preferences.getString("token",""));
//                return headers;
//            }
//            @Override
//            protected java.util.Map<String, String> getParams() {
//                java.util.Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//        };
//          request.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        AppController.getInstance().addToRequestQueue(request, "srarequest");
//    }
    private void getLocalData() {
         OfflineDB db=new OfflineDB(getActivity(),null,null,Constants.DBVERSION);
         OfflineDBProvider d=db.getdataByID(ID);
//         compname.setText(d.getCompname());
//         clientname.setText(d.getName());
//         date.setText(d.getDate());
        Log.d("aaaaaa", "" + d.getDuration());
        Log.d("aaaaaa", "task" + d.getTaskID());
        Log.d("aaaaaa", "project" + d.getProjectID());
        Log.d("aaaaaa", "project" + d.getProjectName());
        Log.d("aaaaaa", "project" + d.getTaskName());

        projectID = d.getProjectID();
        taskID = d.getTaskID();

         clientid=d.getClientID();
         duration.setText(d.getDuration());
         String[] split=d.getStarttime().split(" ");
         stime.setText(split[1]);
         split=d.getEndtime().split(" ");
         etime.setText(split[1]);

         if(!projectID.isEmpty()) {
             projectEt.setText(d.getProjectName());
         }
        if(!taskID.isEmpty()) {
            taskEt.setText(d.getTaskName());
        }

        if (d.getTimeregistered().equals("true"))
        {
            registerTime.setText("Time Already Registered");
        }

//         try {
//             if (d.getCompname().equals("Null")||d.getCompname().equals("Select Name ˅")) {
//                 if (Constants.hasInternetConnectivity(getActivity()))
//                 {
//                       getData("first");
//                 }
//                 else
//                     constants.showDialog("No Internet Connection","Active internet connection is required to complete the process");
//             }
//         }catch (Exception ex)
//         {
//             Log.d("Error", "getLocalData:");
//         }
    }
    private void SelectNameAndStatus() {
        String[] data=new String[clients.size()] ;
        Set set = clients.entrySet();
        Iterator iterator = set.iterator();
        int j=0;
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            data[j++]=mentry.getKey().toString();
            System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Company Name");
        builder.setItems(data, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Object key = clients.keySet().toArray(new Object[clients.size()])[which];
                Object value = clients.get(key);
//                compname.setText(key.toString());
                clientid=value.toString();
                Log.d(key.toString(), "name ");
                Log.d(value.toString(), "ID ");
                OfflineDB db=new OfflineDB(getActivity(),null,null,Constants.DBVERSION);
                db.update(ID,key.toString(),"Active",value.toString());
                //db.updateByName(clientname.getText().toString(),compname.getText().toString(),"Active");
            }
        });
        builder.show();
    }
    private void parsejson(String json) {

        Log.d(CLIENTNAME, "parsejson: ");

        clientstemp=new ArrayList<>();
        Boolean isFound=false;
        JSONObject obj=null;
        try
        {
          obj=new JSONObject(json);
           // Log.d("1", "parsejson: ");
        }catch (Exception EX)
        {
            Log.d("error1", "parsejson: ");
        }

        boolean b=false;
        try {
             b = obj.getBoolean("success");
           // Log.d("2", "parsejson: ");
        }catch (Exception ex)
        {
            Log.d("error2", "parsejson: ");
        }


            if (b)
            {
                try {
                    obj = obj.getJSONObject("result");
             //       Log.d("3", "parsejson: ");
                }catch (Exception ex)
                {
                    Log.d("error3", "parsejson: ");
                }
                JSONArray items=null;
                try {
                    items = obj.getJSONArray("items");
               //     Log.d("4", "parsejson: ");
                }catch (Exception ex)
                {
                    Log.d("error4", "parsejson: ");
                }


                if (items.length()>0)
                {
                    clients=new TreeMap<>();

                    for (int i=0;i<items.length();i++)
                    {
                        try {
                            obj = items.getJSONObject(i);
                 //           Log.d("5", "parsejson: ");
                        }catch (Exception ex)
                        {
                            Log.d("error5", "parsejson: ");
                        }

                        String checker="";
                        try {
                            obj.getString("name").replace(" ", "");
                            clientstemp.add(obj.getString("name"));
                   //         Log.d("6", "parsejson: ");
                            Log.d(obj.getString("name"), "name: ");
                        }catch (Exception ex)
                        {
                            Log.d("error6", "parsejson: ");
                        }
                        OfflineDB db=new OfflineDB(getActivity(),null,null,Constants.DBVERSION);
                        OfflineDBProvider d=db.getdataByID(ID);
                        String s1=d.getName();
                        String s2="";
                        try {
                             s2 = obj.getString("name");
                        }catch (Exception ex)
                        {
                            Log.d("S2error", "parsejson: ");
                        }
                        Log.d(s1, "s1");
                        Log.d(s2, "s2");
                        try {
                            if (s1.equals(s2)) {
                                Log.d("matched", "parsejson: ");
                                isFound = true;
//                                try {
//                                    compname.setText(obj.getString("name"));
//                                } catch (Exception ex) {
//                                    Log.d("error7", "parsejson: ");
//                                }
                                 db = new OfflineDB(getActivity(), null, null, 1);
                                try {
//                                    db.update(ID, compname.getText().toString(), "Active", obj.getInt("id") + "");
                                } catch (Exception ex) {
                                    Log.d("error9", "parsejson: ");
                                }
                                break;
                            } else {
                                try {
                                    clients.put(obj.getString("name"), obj.getInt("id"));
                                     } catch (Exception ex) {
                                    Log.d("error8", "parsejson: ");
                                }
                            }
                        }catch (Exception ex)
                        {
                            Log.d("error101", "parsejson: ");
                        }
                    }
                    if (isFound==false){
                        Log.d("786", "parsejson: ");
//                        compname.setText("Select Name ˅");
                       // status.setText("Unknown");
                    }
                    Log.d("Succeeded", "parsejson: ");
               }else {
                    Log.d("0 length", "parsejson:");
                }
            }else{
                Log.d("Error", "False:JSON");
            }



    }
    private void parsejsonrepaet(String json) {
        try
        {
            JSONObject obj=new JSONObject(json);
            if (obj.getBoolean("success"))
            {
                obj = obj.getJSONObject("result");
                JSONArray items=obj.getJSONArray("items");
                if (items.length()>0)
                {
                    clients=new TreeMap<>();
                    for (int i=0;i<items.length();i++)
                    {
                        obj = items.getJSONObject(i);
                        clients.put(obj.getString("name"),obj.getInt("id"));
                    }
                    SelectNameAndStatus();
                    Log.d("Succeeded", "parsejson: ");
                }else
                {
                    Log.d("0 length", "parsejson:");
                }
            }else
                {
                Log.d("Error", "False:JSON");
            }
        }catch (Exception ex)
        {
            Log.d(ex.getStackTrace()+"", "parsejson: ");
        }
    }

    void setSpins(View view) {
        projectEt = view.findViewById(R.id.projectEt);
        productSpin = view.findViewById(R.id.productSpin);
        projectView = view.findViewById(R.id.projectView);

        taskSpin = view.findViewById(R.id.taskSpin);
        taskEt = view.findViewById(R.id.taskEt);
        taskView = view.findViewById(R.id.taskView);

        projectSpinnerArrayAdapter = new MySpinnerAdapter(
                getActivity(),R.layout.custom_spin_item, projectList);
        taskSpinnerArrayAdapter =  new MySpinnerAdapter(
                getActivity(),R.layout.custom_spin_item, taskList);
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
                                Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();
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
                                Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();
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

    String[] getHoursDetail(String resultStr1) {

        String[] temp = resultStr1.split(":");
        String resultStr = temp[0] + ":" + temp[1];

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

//    private void getData(final String caller) {
//        constants.showProgress("Please wait...");
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                preferences.getString("URL","")+"/api/services/app/client/GetAllClients",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("onResponse:", response);
//                        constants.endProgress();
//                        if (caller.equals("first")) {
//                            parsejson(response);
//                        }
//                        else if (caller.equals("repeat")) {
//                            parsejsonrepaet(response);
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        constants.endProgress();
//                        // As of f605da3 the following should work
//                        NetworkResponse response = error.networkResponse;
//                        if (error instanceof ServerError && response != null) {
//                            try {
//                                String res = new String(response.data,
//                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//                                // Now you can use any deserializer to make sense of data
//                                //JSONObject obj = new JSONObject(res);
//                                System.out.print(res);
//                                Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();
//                            } catch (UnsupportedEncodingException e1) {
//                                // Couldn't properly decode data to string
//                                Log.d("err4", "onErrorResponse: ");
//                            }
//                        }
//                    }
//                }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//            @Override
//            public Map< String, String > getHeaders() throws AuthFailureError {
//                HashMap< String, String > headers = new HashMap < String, String > ();
//                headers.put("Content-Type","application/json; charset=utf-8");
//                headers.put("Authorization", "Bearer " +preferences.getString("token",""));
//                return headers;
//            }
//            @Override
//            protected java.util.Map<String, String> getParams() {
//                java.util.Map<String, String> params = new HashMap<String, String>();
//                // params.put("UserID", constants.getValue("UserID"));
//                return params;
//            }
//
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        AppController.getInstance().addToRequestQueue(request, "srarequest");
//    }

}
