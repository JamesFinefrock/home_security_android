package com.finefrock.james.security;

import java.util.Comparator;

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
    public boolean open_status;

    private SecuritySwitch() {

    }

    public SecuritySwitch(long id, String name, Boolean open_status) {
        this.id = (int) id;
        this.name = name;
        this.open_status = open_status;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nStatus: " + open_status;
    }
}