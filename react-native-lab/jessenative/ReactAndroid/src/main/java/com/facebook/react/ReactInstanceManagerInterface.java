package com.facebook.react;


import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.List;

public interface ReactInstanceManagerInterface {

    List<ViewManager> getOrCreateViewManagers(
            ReactApplicationContext catalystApplicationContext);
    ViewManager createViewManager(String viewManagerName);
    List<String> getViewManagerNames();
    ReactContext getCurrentReactContext();
    boolean hasStartedCreatingInitialContext();
    public void createReactContextInBackground();
    void detachRootView(ReactRootView rootView);
    void attachRootView(ReactRootView rootView);
}
