package com.example.nomanahmed.portlr.DataProviders;

/**
 * Created by Noman Ahmed on 4/16/2019.
 */

public class OfflineDBProvider {
    String ID;
    String number;
    String name;
    String duration;
    String status;
    String date;
    String starttime;
    String endtime;
    String compname;
    String timeregistered;
    String clientID;
    String taskID;
    String projectID;
    String taskName;
    String projectName;
    String isDelete;

    public OfflineDBProvider(String ID, String number, String name, String duration, String status, String date, String starttime, String endtime, String compname, String timeregistered, String clientID, String taskID, String projectID, String taskName, String projectName, String isDelete, String calltype) {
        this.ID = ID;
        this.number = number;
        this.name = name;
        this.duration = duration;
        this.status = status;
        this.date = date;
        this.starttime = starttime;
        this.endtime = endtime;
        this.compname = compname;
        this.timeregistered = timeregistered;
        this.clientID = clientID;
        this.taskID = taskID;
        this.projectID = projectID;
        this.taskName = taskName;
        this.projectName = projectName;
        this.isDelete = isDelete;
        this.calltype = calltype;
    }

    String calltype;

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public String getClientID() {
        return clientID;
    }

    public String getTaskID() {
        return taskID;
    }

    public String getProjectID() {
        return projectID;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public OfflineDBProvider(String ID, String number, String name, String duration, String status, String date, String starttime, String endtime, String compname, String timeregistered, String clientID) {

        this.ID = ID;
        this.number = number;
        this.name = name;
        this.duration = duration;
        this.status = status;
        this.date = date;
        this.starttime = starttime;
        this.endtime = endtime;
        this.compname = compname;
        this.timeregistered = timeregistered;
        this.clientID = clientID;
    }



    public OfflineDBProvider() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getCompname() {
        return compname;
    }

    public void setCompname(String compname) {
        this.compname = compname;
    }

    public OfflineDBProvider(String ID, String number, String name, String duration, String status, String date, String starttime, String endtime, String compname) {

        this.ID = ID;
        this.number = number;
        this.name = name;
        this.duration = duration;
        this.status = status;
        this.date = date;
        this.starttime = starttime;
        this.endtime = endtime;
        this.compname = compname;
    }

    public String getTimeregistered() {
        return timeregistered;
    }

    public void setTimeregistered(String timeregistered) {
        this.timeregistered = timeregistered;
    }

    public OfflineDBProvider(String ID, String number, String name, String duration, String status, String date, String starttime, String endtime, String compname, String timeregistered) {

        this.ID = ID;
        this.number = number;
        this.name = name;
        this.duration = duration;
        this.status = status;
        this.date = date;
        this.starttime = starttime;
        this.endtime = endtime;
        this.compname = compname;
        this.timeregistered = timeregistered;
    }
}
