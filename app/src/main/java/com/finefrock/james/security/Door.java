package com.finefrock.james.security;

import android.support.annotation.Keep;

/**
 * Created by james on 12/17/17.
 */

public class Door {
    public String name;
    public boolean open_status;

    private Door() {

    }

    public Door(String name, Boolean open_status) {
        this.name = name;
        this.open_status = open_status;
    }
}
