package com.example.applicationvidadetopografo.Providers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.applicationvidadetopografo.Activity.MainActivity;
import com.example.applicationvidadetopografo.R;

import agency.tango.materialintroscreen.SlideFragment;

public class SlideApresentation extends SlideFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_slide_apresentation, container, false);
        return view;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getActivity().getResources().getString(R.string.slide_4_checkbox_error);
    }

    @Override
    public int backgroundColor() {
        return buttonsColor();
    }

    @Override
    public int buttonsColor() {
        return R.color.colorAccent;
    }
}

