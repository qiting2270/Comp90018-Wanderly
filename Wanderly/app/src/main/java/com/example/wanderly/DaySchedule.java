package com.example.wanderly;

import java.util.List;

public class DaySchedule {
    private List<Integer> days;
    private List<String> dates;
    private List<HomeSchedule> stops;

    public DaySchedule(List<Integer> days, List<String> dates, List<HomeSchedule> stops) {
        this.days = days;
        this.dates = dates;
        this.stops = stops;
    }

    public List<Integer> getDays() {
        return days;
    }

    public List<String> getDates() {
        return dates;
    }

    public List<HomeSchedule> getStops() {
        return stops;
    }

    public void setStops(List<HomeSchedule> stops) {
        this.stops = stops;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }
}
