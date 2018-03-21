package com.nobody.spoon.app;

import android.app.Activity;
import android.app.Application;

import com.nobody.spoon.di.component.AppComponent;
import com.nobody.spoon.di.component.DaggerAppComponent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Robin on 2018/3/20.
 */

public class App extends Application {

    private static App instance;
    public static AppComponent appComponent;
    private Set<Activity> allActs;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static App getInstance() {
        return instance;
    }


    public static AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .build();
        }

        return appComponent;
    }

    public void removeAct(Activity act) {
        if (allActs != null) allActs.remove(act);
    }

    public void addActivity(Activity act) {
        if (allActs == null) allActs = new HashSet<>();
        allActs.add(act);
    }
}