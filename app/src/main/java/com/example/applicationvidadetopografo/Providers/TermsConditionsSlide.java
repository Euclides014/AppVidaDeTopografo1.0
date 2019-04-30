package com.example.applicationvidadetopografo.Providers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.applicationvidadetopografo.R;

import agency.tango.materialintroscreen.SlideFragment;

public class TermsConditionsSlide extends SlideFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_terms_conditions_slides,container,false);
    }

    @Override
    public boolean canMoveFurther() {
        CheckBox check_term;
        check_term = getId();

        return super.canMoveFurther();
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
