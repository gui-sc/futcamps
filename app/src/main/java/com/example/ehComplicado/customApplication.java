package com.example.ehComplicado;

import android.app.Application;

import com.firebase.client.Firebase;

public class customApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
