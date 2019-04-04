package com.example.applicationvidadetopografo.Providers;

import android.os.AsyncTask;

import com.example.applicationvidadetopografo.Activity.FormularioActivity;
import com.example.applicationvidadetopografo.Classes.Endereco;
import com.example.applicationvidadetopografo.Classes.Usuario;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

public class AdreessRequest extends AsyncTask<Void, Void, Endereco> {
    private WeakReference<FormularioActivity> activity;

    public AdreessRequest (FormularioActivity activity){
        this.activity = new WeakReference<>(activity);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(activity.get() != null){
            activity.get().lockFields(true);
        }
    }

    @Override
    protected Endereco doInBackground(Void... voids) {
        try {
            String jsonString = JsonRequest.request(activity.get().getUriCEP());
            Gson gson = new Gson();
            return gson.fromJson(jsonString, Endereco.class);
        } catch (Exception e){
         e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Endereco endereco) {
        super.onPostExecute(endereco);
        if(activity.get() != null){
            activity.get().lockFields(false);

            if(endereco != null){
                activity.get().setDataViews( endereco );
            }
        }
    }
}
