package com.example.samples2facebook;

import com.facebook.FacebookSdk;

import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		FacebookSdk.sdkInitialize(this);
	}
}
