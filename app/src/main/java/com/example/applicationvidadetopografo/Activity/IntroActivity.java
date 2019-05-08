package com.example.applicationvidadetopografo.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.applicationvidadetopografo.Helper.SPInfo;
import com.example.applicationvidadetopografo.Providers.SlideApresentation;
import com.example.applicationvidadetopografo.Providers.TermsConditionsSlide;
import com.example.applicationvidadetopografo.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide( new SlideFragmentBuilder()
                .backgroundColor(R.color.coloIntro1)
                .buttonsColor(R.color.colorAccent)
                .title(getResources().getString(R.string.slide_1_title))
                .description(getResources().getString(R.string.slide_1_description))
                .image(R.drawable.logo_app)
                .build()

        );
        String [] PERMISSION_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        addSlide( new SlideFragmentBuilder()
                .backgroundColor(R.color.coloIntro1)
                .buttonsColor(R.color.colorAccent)
                .title(getResources().getString(R.string.slide_2_title))
                .description(getResources().getString(R.string.slide_2_description))
                .image(R.drawable.icon_globo)
                .neededPermissions(PERMISSION_LOCATION)
                .build()

        );

        addSlide(new SlideApresentation());

        addSlide(new TermsConditionsSlide());
    }

    /*private void verfyIntroActivity(){
        if (new SPInfo(this).isIntroActivityShown()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            finish();
        }
    }*/
}
