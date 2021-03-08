package com.example.nomanahmed.portlr.Model;

import com.example.nomanahmed.portlr.LocalDatabase.NewConstants;
import com.example.nomanahmed.portlr.Utils.DateTimeUtils;

import java.util.Date;

public class TimeLog {
    public int timesheetId;
    public int userId;
    public int projectId;
    public String projectName;
    public int taskId;
    public String taskName;
    public String comment;
    public String startAt;
    public String endAt;
    public Double hours;
    public String spentAt;
    public Date creationTime;

    Boolean filtered(String time) {
        return DateTimeUtils.getLocalDateString(creationTime).equals(time);
    }

    public TimeLog() {
        timesheetId = 0;
        userId = NewConstants.DEFAULT_USER_ID;
        projectId = NewConstants.DEFAULT_PROJECT_ID;
        projectName = "";
        taskId = NewConstants.DEFAULT_TASK_ID;
        taskName = "";
        comment = "";
        startAt = "";
        endAt = "";
        hours = 0.0;
        spentAt = "";
        creationTime = new Date();
    }

}
