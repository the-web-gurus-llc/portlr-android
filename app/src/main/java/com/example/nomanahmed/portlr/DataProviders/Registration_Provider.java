package com.example.nomanahmed.portlr.DataProviders;

/**
 * Created by Noman Ahmed on 4/14/2019.
 */

public class Registration_Provider {
    String date, time, name,startime,endtime;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartime() {
        return startime;
    }

    public void setStartime(String startime) {
        this.startime = startime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public Registration_Provider(String date, String time, String name, String startime, String endtime) {

        this.date = date;
        this.time = time;
        this.name = name;
        this.startime = startime;
        this.endtime = endtime;
    }
}
