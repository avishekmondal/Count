package com.interfaces;

/**
 * Created by Rahul on 5/6/2015.
 */
public interface BackgroundTaskInterface {
    public void onStarted();
    public void onCompleted(String jsonStr);
}
