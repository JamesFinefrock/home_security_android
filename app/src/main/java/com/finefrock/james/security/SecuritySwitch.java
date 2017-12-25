package com.finefrock.james.security;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by james on 12/17/17.
 */

public class SecuritySwitch {

    public static class SecuritySwitchComparator implements Comparator<SecuritySwitch> {
        public int compare(SecuritySwitch left, SecuritySwitch right) {
            return left.id - right.id;
        }
    }

    public int id;
    public String name;
    public boolean open;
    public int last_opened;

    private SecuritySwitch() {

    }

    public SecuritySwitch(long id, String name, Boolean open, int last_opened) {
        this.id = (int) id;
        this.name = name;
        this.open = open;
        this.last_opened = last_opened;
    }

    @Override
    public String toString() {
        long milliseconds = Long.valueOf(last_opened + 18000)*1000;// its need to be in milisecond
        Date date = new java.util.Date(milliseconds);
        String dateString = new SimpleDateFormat("hh:mm a MM dd, yyyy ").format(milliseconds);

        return "Name: " + name + "\nOpen: " + open + "\nLast Opened: " + dateString;
    }
}
