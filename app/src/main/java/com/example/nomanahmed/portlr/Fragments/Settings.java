package com.example.nomanahmed.portlr.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nomanahmed.portlr.Activities.Home;
import com.example.nomanahmed.portlr.R;

/**
 * Created by Noman Ahmed on 4/26/2019.
 */

public class Settings extends Fragment {
//    ImageView menu;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, null, false);
        preferences=getActivity().getSharedPreferences("OtherSettings", Context.MODE_PRIVATE);
        editor=preferences.edit();
        editor.putString("Screen","Settings");
        editor.commit();
        editor.apply();
//        menu=(ImageView)view.findViewById(R.id.menu);
//        menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Home.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                Home.mDrawerLayout.openDrawer(Gravity.LEFT);
//                Home.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//            }
//        });
        TextView txt1,txt2;
        txt1=(TextView)view.findViewById(R.id.t1);
        txt2=(TextView)view.findViewById(R.id.t2);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/concertone-regular.ttf");
        txt1.setTypeface(face);
        face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");
        txt2.setTypeface(face);
        return view;
    }
}
