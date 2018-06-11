package com.cityzipcorp.customer.model;

public enum WEEKDAY {

    MONDAY("monday"),
    TUESDAY("tuesday"),
    WEDNESDAY("wednesday"),
    THURSDAY("thursday"),
    FRIDAY("friday"),
    SATURDAY("saturday"),
    SUNDAY("sunday") ;

    private String weekday;

    WEEKDAY(String weekday) {
        this.weekday = weekday;
    }

    public String getWeekday() {
        return weekday;
    }
}
