package com.example.applicationvidadetopografo.Helper;

import android.content.Context;

public class SPInfo{
    Context context;
    public SPInfo(Context context){

    }

    public Context updateIntroStatus(boolean status){
        context.getSharedPreferences("PREF", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("status", status)
                .apply();
        return context;
    }

    public boolean isIntroActivityShown(){
        context.getSharedPreferences("PREF", Context.MODE_PRIVATE)
                .getBoolean("status",false);

        return true;
    }
}
