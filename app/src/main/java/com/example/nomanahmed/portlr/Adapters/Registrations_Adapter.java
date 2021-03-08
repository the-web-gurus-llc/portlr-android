package com.example.nomanahmed.portlr.Adapters;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nomanahmed.portlr.Fragments.ClientDetails;
import com.example.nomanahmed.portlr.Fragments.Registration;
import com.example.nomanahmed.portlr.Helper.Constants;
import com.example.nomanahmed.portlr.DataProviders.OfflineDBProvider;
import com.example.nomanahmed.portlr.LocalDatabase.OfflineDB;
import com.example.nomanahmed.portlr.R;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
public class Registrations_Adapter extends BaseAdapter implements Filterable{
    Context context;
    Constants constants;
    ArrayList<Bitmap> allbitmaps;
    ArrayList<String> urls;
    ArrayList<OfflineDBProvider> serviceManualArrayList,arrayList;
    LayoutInflater inflater;
    FragmentTransaction transaction;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Registration parentFrag;

    public Registrations_Adapter(Context context, ArrayList<OfflineDBProvider> list, FragmentTransaction transaction, Registration parentFrag) {
        this.context = context;
        serviceManualArrayList = list;
        inflater = LayoutInflater.from(context);
        allbitmaps=new ArrayList<>();
        urls=new ArrayList<>();
        this.transaction=transaction;
        constants=new Constants(context);
        preferences=context.getSharedPreferences("OtherSettings",Context.MODE_PRIVATE);
        editor=preferences.edit();
        this.parentFrag = parentFrag;
    }
    @Override
    public int getCount() {
        return serviceManualArrayList.size();
    }
    @Override
    public Object getItem(int i) {
        return i;
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            final Holder holder;
            holder = new Holder();
            view = inflater.inflate(R.layout.reg_listitem, viewGroup, false);
            holder.id = (TextView) view.findViewById(R.id.id);
            holder.startime = (TextView) view.findViewById(R.id.starttime);
//            holder.endtime = (TextView) view.findViewById(R.id.endtime);
            holder.time = (TextView) view.findViewById(R.id.time);
            holder.image = (ImageView) view.findViewById(R.id.status);
            holder.delete = view.findViewById(R.id.delete);
//            holder.calltype = (ImageView) view.findViewById(R.id.calltype);
            view.setTag(holder);
            OfflineDBProvider e = serviceManualArrayList.get(i);
            String[] split=e.getStarttime().split(" ");
            String date=split[0];
            split=date.split("/");
//            if (e.getName().equals("Unknown"))
//            {
//                holder.clientname.setText(e.getNumber());
//            }else
//            {
//                holder.clientname.setText(e.getName());
//            }
            holder.time.setText(e.getDuration());
            holder.id.setText(e.getID());
            holder.startime.setText(DateTimeUtils.getStrimgToStringForReg(e.getStarttime()) + " - " + DateTimeUtils.getStrimgToStringForReg(e.getEndtime()));
//            holder.endtime.setText(e.getEndtime());
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putString("Screen","ClientDetails");
                    if (Registration.searchtxt.getText().toString().length()>0)
                    editor.putString("RecentDate", Registration.searchtxt.getText().toString());
                    editor.commit();
//                    Log.d(holder.clientname.getText().toString(), "onClick: ");
                    ClientDetails.ID=holder.id.getText().toString();
//                    ClientDetails.CLIENTNAME=holder.clientname.getText().toString();
                    transaction.replace(R.id.containerView,new ClientDetails()).commit();
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    OfflineDB db=new OfflineDB(context,null,null,Constants.DBVERSION);
                                    db.deleteRegistration(holder.id.getText().toString());
                                    parentFrag.Refresh();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                }
            });
            if (e.getCalltype().equals("Incoming"))
            {
//                holder.calltype.setBackgroundResource(R.drawable.incomingcall);
            }else if (e.getCalltype().equals("Outgoing"))
            {
//            holder.calltype.setBackgroundResource(R.drawable.outgoingcall);
            }
//            if (e.getTimeregistered().equals("true")) holder.image.setBackgroundResource(R.drawable.active);
//            else holder.image.setBackgroundResource(R.drawable.unactive);
        }catch (Exception ex)
        {
            Log.d("error", "getView: ");
        }
        return view;
    }
    class Holder {
        TextView startime,id,time;
        ImageView image;
        ImageView delete;
//        ImageView calltype;
    }
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // results of a
                // filtering
                // operation in
                // values
                // List<String> FilteredArrList = new ArrayList<String>();
                List<OfflineDBProvider> FilteredArrList = new ArrayList<OfflineDBProvider>();
                if (arrayList == null) {
                    arrayList = new ArrayList<OfflineDBProvider>(serviceManualArrayList); // saves
                }
                /********
                 *
                 * If constraint(CharSequence that is received) is null returns
                 * the mOriginalValues(Original) values else does the Filtering
                 * and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {
                    results.count = arrayList.size();
                    results.values = arrayList;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < arrayList.size(); i++) {
                        OfflineDBProvider currentsdItems = arrayList.get(i);
                        String[] split = currentsdItems.getStarttime().split(" ");
                        String date=split[0];
                        split=date.split("/");
                        date=split[1]+"-"+split[0]+"-"+split[2];

                        if (date.toLowerCase().contains(constraint.toString()) && currentsdItems.getIsDelete().equals("false")) {
                            FilteredArrList.add(currentsdItems);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                serviceManualArrayList = (ArrayList<OfflineDBProvider>) results.values; // has
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
