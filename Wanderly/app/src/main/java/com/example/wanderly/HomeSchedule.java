package com.example.wanderly;

import android.widget.ImageView;

public class HomeSchedule {
    private String name, time_from, time_to;
    ImageView icon;

    public HomeSchedule() {
    }

    public HomeSchedule(String name, String time_from, String time_to) {
        this.name = name;
        this.time_from = time_from;
        this.time_to = time_to;
//        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }
}
