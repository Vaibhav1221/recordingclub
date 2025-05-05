package com.videvelopers.app.vuh.app_helpers;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class AppActionBar {

    private final Context context;
    private final AppCompatActivity activity;

    public AppActionBar(Context context,AppCompatActivity activity){
        this.activity=activity;
        this.context=context;
    }

    public void setCustomActionBarWithBackButton(String button_text){
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeActionContentDescription(button_text);
    }

}
