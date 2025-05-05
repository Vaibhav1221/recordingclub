package com.videvelopers.app.vuh;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.videvelopers.app.vuh.app_components.AppCore;
import com.videvelopers.app.vuh.app_helpers.AppActionBar;
import com.videvelopers.app.vuh.app_helpers.AppFunctions;
import com.videvelopers.app.vuh.app_helpers.FileHelper;
import com.videvelopers.app.vuh.app_helpers.URLHelpers;
import com.videvelopers.app.vuh.database_helpers.DatabaseHelpers;

public class AppInitializer {

    private final Context context;
    private final AppCompatActivity activity;
    private final DatabaseHelpers databaseHelpers;
    private final URLHelpers urlHelpers;
    private final AppActionBar appActionBar;
    private final AppFunctions appFunctions;
    private final FileHelper fileHelper;

    public AppInitializer(Context context,AppCompatActivity activity) {
        this.activity=activity;
        this.context=context;
        databaseHelpers=new DatabaseHelpers(context);
        urlHelpers=new URLHelpers();
        appActionBar=new AppActionBar(context,activity);
        appFunctions=new AppFunctions(context,activity);
        fileHelper=new FileHelper(context);
    }

    public URLHelpers getURLHelpers(){
        return urlHelpers;
    }

    public AppCore getAppCore() {
        AppCore appCore=new AppCore();
        return appCore;
    }

    public DatabaseHelpers getDatabaseHelpers() {
        return databaseHelpers;
    }

    public AppActionBar getAppActionBar(){
        return appActionBar;
    }

    public AppFunctions getAppFunctions(){
        return appFunctions;
    }

    public FileHelper getFileHelper() {
        return fileHelper;
    }

}
