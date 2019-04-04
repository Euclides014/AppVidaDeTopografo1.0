package com.example.applicationvidadetopografo.Providers;

import android.app.Activity;

public class Util {
    private Activity activity;
    private int ids[];

    public Util (Activity activity, int... ids){
        this.activity = activity;
        this.ids = ids;
    }

    public void lockFields (boolean isToloock){
        for (int id : ids){
            setLockField(id, isToloock);
        }
    }

    private void setLockField (int fieldId, boolean isToLock){
        activity.findViewById(fieldId).setEnabled(!isToLock);
    }
}
