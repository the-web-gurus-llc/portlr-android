package com.example.nomanahmed.portlr.Model;

import android.content.Context;

import com.example.nomanahmed.portlr.Utils.SharedPrefUtils;

import java.util.Date;

import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_CHECKIN;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_CHECKOUT;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_DATE;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_END_DATE;
import static com.example.nomanahmed.portlr.LocalDatabase.NewConstants.PREF_START_DATE;


public class CheckInOut {

    private Context mContext;

    public Date date;
    public boolean checkin;
    public boolean checkout;
    public Date startDate;
    public Date endDate;

    public CheckInOut(Context context) {
        mContext = context;
    }

    public void saveObj() {
        new SharedPrefUtils(mContext).addValue(date, PREF_DATE);
        new SharedPrefUtils(mContext).addValue(checkin, PREF_CHECKIN);
        new SharedPrefUtils(mContext).addValue(checkout, PREF_CHECKOUT);
        new SharedPrefUtils(mContext).addValue(startDate, PREF_START_DATE);
        new SharedPrefUtils(mContext).addValue(endDate, PREF_END_DATE);
    }

    public void loadObj() {
        date = new SharedPrefUtils(mContext).getDate(PREF_DATE);
        checkin = new SharedPrefUtils(mContext).getBoolean(PREF_CHECKIN);
        checkout = new SharedPrefUtils(mContext).getBoolean(PREF_CHECKOUT);
        startDate = new SharedPrefUtils(mContext).getDate(PREF_START_DATE);
        endDate = new SharedPrefUtils(mContext).getDate(PREF_END_DATE);
    }

    public void clearObj() {
        new SharedPrefUtils(mContext).addValue(new Date(), PREF_DATE);
        new SharedPrefUtils(mContext).addValue(false, PREF_CHECKIN);
        new SharedPrefUtils(mContext).addValue(false, PREF_CHECKOUT);
        new SharedPrefUtils(mContext).addValue(new AutoTimeTrack(mContext).getStartTime(), PREF_START_DATE);
        new SharedPrefUtils(mContext).addValue(new AutoTimeTrack(mContext).getEndTime(), PREF_END_DATE);
    }

}
