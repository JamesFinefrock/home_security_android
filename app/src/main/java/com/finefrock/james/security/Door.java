package com.finefrock.james.security;

import android.support.annotation.Keep;

import java.util.Comparator;

/**
 * Created by james on 12/17/17.
 */

public class Door {

    public static class DoorComparator implements Comparator<Door> {
        public int compare(Door left, Door right) {
            return left.id - right.id;
        }
    }

    public int id;
    public String name;
    public boolean open_status;

    private Door() {

    }

    public Door(long id, String name, Boolean open_status) {
        this.id = (int) id;
        this.name = name;
        this.open_status = open_status;
    }
}
