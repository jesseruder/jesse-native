// Copyright 2004-present Facebook. All Rights Reserved.

package com.jessenative;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.CoreModulesPackage;
import com.facebook.react.NativeModuleRegistryBuilder;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerInterface;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactPackageLogger;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.NativeModuleRegistry;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.devsupport.DoubleTapReloadRecognizer;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.react.uimanager.UIImplementationProvider;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.facebook.react.bridge.ReactMarkerConstants.CREATE_VIEW_MANAGERS_END;
import static com.facebook.react.bridge.ReactMarkerConstants.CREATE_VIEW_MANAGERS_START;
import static com.facebook.systrace.Systrace.TRACE_TAG_REACT_JAVA_BRIDGE;

public class JesseReactActivityDelegate implements ReactInstanceManagerInterface {

    private final @Nullable Activity mActivity;
    private final @Nullable FragmentActivity mFragmentActivity;
    private final @Nullable String mMainComponentName;

    private @Nullable
    ReactRootView mReactRootView;
    private @Nullable DoubleTapReloadRecognizer mDoubleTapReloadRecognizer;
    private @Nullable PermissionListener mPermissionListener;
    private @Nullable Callback mPermissionsCallback;

    public JesseReactActivityDelegate(Activity activity, @Nullable String mainComponentName) {
        mActivity = activity;
        mMainComponentName = mainComponentName;
        mFragmentActivity = null;
    }

    public JesseReactActivityDelegate(
            FragmentActivity fragmentActivity,
            @Nullable String mainComponentName) {
        mFragmentActivity = fragmentActivity;
        mMainComponentName = mainComponentName;
        mActivity = null;
    }

    protected @Nullable Bundle getLaunchOptions() {
        return null;
    }

    protected ReactRootView createRootView() {
        return new ReactRootView(getContext());
    }

    /**
     * Get the {@link ReactNativeHost} used by this app. By default, assumes
     * {@link Activity#getApplication()} is an instance of {@link ReactApplication} and calls
     * {@link ReactApplication#getReactNativeHost()}. Override this method if your application class
     * does not implement {@code ReactApplication} or you simply have a different mechanism for
     * storing a {@code ReactNativeHost}, e.g. as a static field somewhere.
     */
    protected ReactNativeHost getReactNativeHost() {
        return ((ReactApplication) getPlainActivity().getApplication()).getReactNativeHost();
    }

    public ReactInstanceManager getReactInstanceManager() {
        return getReactNativeHost().getReactInstanceManager();
    }

    protected void onCreate(Bundle savedInstanceState) {
        if (mMainComponentName != null) {
            loadApp(mMainComponentName);
        }
        mDoubleTapReloadRecognizer = new DoubleTapReloadRecognizer();
    }

    ReactContext mReactContext;

    protected void loadApp(String appKey) {
        if (mReactRootView != null) {
            throw new IllegalStateException("Cannot loadApp while app is already running.");
        }
        mReactRootView = createRootView();
        mReactRootView.setRootViewTag(1);
        /*mReactRootView.startReactApplication(
                getReactNativeHost().getReactInstanceManager(),
                appKey,
                getLaunchOptions());*/


        // Comment out //catalystInstance.runJSBundle(); in ReactInstanceManager

        ReactApplicationContext reactApplicationContext = new ReactApplicationContext(getContext().getApplicationContext());
        mReactContext = reactApplicationContext;

        ReactChoreographer.initialize();

        //// START OF STUFF
        //getReactNativeHost().getReactInstanceManager().attachRootView(mReactRootView);
        //getReactNativeHost().getReactInstanceManager().getOrCreateViewManagers()
        mPackages.add(new MainReactPackage());
        mPackages.add(
                new CoreModulesPackage(
                        this,
                        new DefaultHardwareBackBtnHandler() {
                            @Override
                            public void invokeDefaultOnBackPressed() {
                                // TODO
                                //ReactInstanceManager.this.invokeDefaultOnBackPressed();
                            }
                        },
                        new UIImplementationProvider(),
                        false,
                        -1));
        //NativeModuleRegistry nativeModuleRegistry = getReactNativeHost().getReactInstanceManager().processPackages(reactContext, mPackages, false);

        // END

        NativeModuleRegistryBuilder nativeModuleRegistryBuilder = new NativeModuleRegistryBuilder(
                reactApplicationContext,
                this,
                false);
        synchronized (mPackages) {
            for (ReactPackage reactPackage : mPackages) {
                try {
                    processPackage(reactPackage, nativeModuleRegistryBuilder);
                } finally {
                    Systrace.endSection(TRACE_TAG_REACT_JAVA_BRIDGE);
                }
            }
        }
        NativeModuleRegistry nativeModuleRegistry;
        try {
            nativeModuleRegistry = nativeModuleRegistryBuilder.build();
        } finally {
        }

        CatalystInstance catalystInstance = new JesseCatalystInstance(nativeModuleRegistry);
        reactApplicationContext.initializeWithInstance(catalystInstance);

        UIManagerModule uiManagerModule = nativeModuleRegistry.getModule(UIManagerModule.class);
        uiManagerModule.addRootView(mReactRootView);



        getPlainActivity().setContentView(mReactRootView);
        mReactRootView.setBackgroundColor(Color.parseColor("#ff0000"));
        mReactRootView.startReactApplication(this, "ModuleName");

        {
            WritableMap m = Arguments.createMap();
            m.putString("text", "Welcome!");
            uiManagerModule.createView(2, "RCTRawText", 1, m);
        }

        {
            WritableMap m = Arguments.createMap();
            m.putBoolean("allowFontScaling", true);
            m.putString("ellipsizeMode", "tail");
            m.putBoolean("accessible", true);
            m.putInt("margin", 10);
            m.putString("textAlign", "center");
            m.putInt("fontSize", 20);
            m.putInt("color", -13421773);
            uiManagerModule.createView(3, "RCTText", 1, m);
        }

        {
            WritableArray a = Arguments.createArray();
            a.pushInt(2);
            uiManagerModule.setChildren(3, a);
        }

        {
            WritableMap m = Arguments.createMap();
            m.putInt("backgroundColor", -321);
            m.putString("justifyContent", "center");
            m.putString("alignItems", "center");
            m.putInt("flex", 1);
            uiManagerModule.createView(4, "RCTView", 1, m);
        }

        {
            WritableArray a = Arguments.createArray();
            a.pushInt(3);
            uiManagerModule.setChildren(4, a);
        }

        {
            WritableArray a = Arguments.createArray();
            a.pushInt(4);
            uiManagerModule.setChildren(1, a);
        }

        uiManagerModule.onBatchComplete();
    }


    private void processPackage(
            ReactPackage reactPackage,
            NativeModuleRegistryBuilder nativeModuleRegistryBuilder) {
        SystraceMessage.beginSection(TRACE_TAG_REACT_JAVA_BRIDGE, "processPackage")
                .arg("className", reactPackage.getClass().getSimpleName())
                .flush();
        if (reactPackage instanceof ReactPackageLogger) {
            ((ReactPackageLogger) reactPackage).startProcessPackage();
        }
        nativeModuleRegistryBuilder.processPackage(reactPackage);

        if (reactPackage instanceof ReactPackageLogger) {
            ((ReactPackageLogger) reactPackage).endProcessPackage();
        }
        SystraceMessage.endSection(TRACE_TAG_REACT_JAVA_BRIDGE).flush();
    }


    private List<ViewManager> mViewManagers;
    private final List<ReactPackage> mPackages = new ArrayList<>();

    public List<ViewManager> getOrCreateViewManagers(
            ReactApplicationContext catalystApplicationContext) {
        ReactMarker.logMarker(CREATE_VIEW_MANAGERS_START);
        Systrace.beginSection(TRACE_TAG_REACT_JAVA_BRIDGE, "createAllViewManagers");
        try {
            if (mViewManagers == null) {
                synchronized (mPackages) {
                    if (mViewManagers == null) {
                        mViewManagers = new ArrayList<>();
                        for (ReactPackage reactPackage : mPackages) {
                            mViewManagers.addAll(reactPackage.createViewManagers(catalystApplicationContext));
                        }
                        return mViewManagers;
                    }
                }
            }
            return mViewManagers;
        } finally {
            Systrace.endSection(TRACE_TAG_REACT_JAVA_BRIDGE);
            ReactMarker.logMarker(CREATE_VIEW_MANAGERS_END);
        }
    }

    @Override
    public ViewManager createViewManager(String viewManagerName) {
        return null;
    }

    @Override
    public List<String> getViewManagerNames() {
        return null;
    }

    @Override
    public ReactContext getCurrentReactContext() {
        return mReactContext;
    }

    @Override
    public boolean hasStartedCreatingInitialContext() {
        return false;
    }

    @Override
    public void createReactContextInBackground() {

    }

    @Override
    public void detachRootView(ReactRootView rootView) {

    }

    @Override
    public void attachRootView(ReactRootView rootView) {

    }


    protected void onPause() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostPause(getPlainActivity());
        }
    }

    protected void onResume() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostResume(
                    getPlainActivity(),
                    (DefaultHardwareBackBtnHandler) getPlainActivity());
        }

        if (mPermissionsCallback != null) {
            mPermissionsCallback.invoke();
            mPermissionsCallback = null;
        }
    }

    protected void onDestroy() {
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
            mReactRootView = null;
        }
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostDestroy(getPlainActivity());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager()
                    .onActivityResult(getPlainActivity(), requestCode, resultCode, data);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance()
                && getReactNativeHost().getUseDeveloperSupport()
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            event.startTracking();
            return true;
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance() && getReactNativeHost().getUseDeveloperSupport()) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
                return true;
            }
            boolean didDoubleTapR = Assertions.assertNotNull(mDoubleTapReloadRecognizer)
                    .didDoubleTapR(keyCode, getPlainActivity().getCurrentFocus());
            if (didDoubleTapR) {
                getReactNativeHost().getReactInstanceManager().getDevSupportManager().handleReloadJS();
                return true;
            }
        }
        return false;
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance()
                && getReactNativeHost().getUseDeveloperSupport()
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
            return true;
        }
        return false;
    }

    public boolean onBackPressed() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onBackPressed();
            return true;
        }
        return false;
    }

    public boolean onNewIntent(Intent intent) {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onNewIntent(intent);
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(
            String[] permissions,
            int requestCode,
            PermissionListener listener) {
        mPermissionListener = listener;
        getPlainActivity().requestPermissions(permissions, requestCode);
    }

    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        mPermissionsCallback = new Callback() {
            @Override
            public void invoke(Object... args) {
                if (mPermissionListener != null && mPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                    mPermissionListener = null;
                }
            }
        };
    }

    private Context getContext() {
        if (mActivity != null) {
            return mActivity;
        }
        return Assertions.assertNotNull(mFragmentActivity);
    }

    private Activity getPlainActivity() {
        return ((Activity) getContext());
    }
}
