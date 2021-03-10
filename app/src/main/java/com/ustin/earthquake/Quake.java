package com.ustin.earthquake;

import java.util.Date;

// класс создает запись в фрагмет для отображения пользователю
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
        return details;
    }
}
