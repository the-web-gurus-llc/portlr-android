package com.example.nomanahmed.portlr.Model;

import com.example.nomanahmed.portlr.LocalDatabase.NewConstants;

public class Project {
    public int userId;
    public String name;

    public Project(int id, String na) {
        userId = id;
        name = na;
    }

    public Project() {
        userId = NewConstants.DEFAULT_USER_ID;
        name = "";
    }
}
