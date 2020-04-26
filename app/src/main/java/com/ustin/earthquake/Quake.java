package com.ustin.earthquake;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Quake {
    private Date date;
    private String details;
    private double magnitude;
    private String location;

    public Date getDate() {
        return date;
    }

    public String getDetails() {
        return details;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }


    public Quake(Date _d, String _det, double _mag, String _loc) {
        date = _d;
        details = _det;
        magnitude = _mag;
        location = _loc;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        String dStr = df.format(date);

        return "Local Time: " + dateString + "\nDate: " + dStr + "\nMagnitude: " + magnitude + "\nDetails: " + details + "\nLocation: " + location;
    }
}
