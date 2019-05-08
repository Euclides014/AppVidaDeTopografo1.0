package com.example.applicationvidadetopografo.Providers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Space;

import com.example.applicationvidadetopografo.Activity.MainActivity;
import com.example.applicationvidadetopografo.Helper.SPInfo;
import com.example.applicationvidadetopografo.R;

import agency.tango.materialintroscreen.SlideFragment;

public class TermsConditionsSlide extends SlideFragment {
    private CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_terms_conditions_slides, container, false);
        checkBox = (CheckBox) view.findViewById(R.id.cb_concordo);
        return view;
    }

    @Override
    public boolean canMoveFurther() {
        if( checkBox.isChecked() ){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();

        }
        return checkBox.isChecked();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getActivity().getResources().getString(R.string.slide_4_checkbox_error);
    }

    @Override
    public int backgroundColor() {
        return R.color.coloIntro1;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorAccent;
    }
}
