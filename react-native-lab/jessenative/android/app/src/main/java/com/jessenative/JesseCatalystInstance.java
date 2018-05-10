package com.jessenative;

import android.os.Build;

import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.CatalystInstanceImpl;
import com.facebook.react.bridge.JSIModule;
import com.facebook.react.bridge.JSIModuleHolder;
import com.facebook.react.bridge.JavaScriptContextHolder;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.JavaScriptModuleRegistry;
import com.facebook.react.bridge.NativeArray;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.NativeModuleRegistry;
import com.facebook.react.bridge.NotThreadSafeBridgeIdleDebugListener;
import com.facebook.react.bridge.queue.MessageQueueThreadSpec;
import com.facebook.react.bridge.queue.QueueThreadExceptionHandler;
import com.facebook.react.bridge.queue.ReactQueueConfiguration;
import com.facebook.react.bridge.queue.ReactQueueConfigurationImpl;
import com.facebook.react.bridge.queue.ReactQueueConfigurationSpec;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by jesseruder on 5/9/18.
 */

public class JesseCatalystInstance implements CatalystInstance {

    JavaScriptModuleRegistry mJSModuleRegistry;
    NativeModuleRegistry mNativeModuleRegistry;
    private final ReactQueueConfigurationImpl mReactQueueConfiguration;

    private class NativeExceptionHandler implements QueueThreadExceptionHandler {
        @Override
        public void handleException(Exception e) {
            // Any Exception caught here is because of something in JS. Even if it's a bug in the
            // framework/native code, it was triggered by JS and theoretically since we were able
            // to set up the bridge, JS could change its logic, reload, and not trigger that crash.

            // TODO
            // onNativeException(e);
        }
    }



    public JesseCatalystInstance(final NativeModuleRegistry nativeModuleRegistry) {
        mJSModuleRegistry = new JavaScriptModuleRegistry();
        mNativeModuleRegistry = nativeModuleRegistry;

        ReactQueueConfigurationSpec mainTHREAD = ReactQueueConfigurationSpec.builder().setJSQueueThreadSpec(MessageQueueThreadSpec.mainThreadSpec()).setNativeModulesQueueThreadSpec(MessageQueueThreadSpec.mainThreadSpec()).build();

        mReactQueueConfiguration = ReactQueueConfigurationImpl.create(
                mainTHREAD,
                new NativeExceptionHandler());
    }

    @Override
    public void handleMemoryPressure(int level) {

    }

    @Override
    public void runJSBundle() {

    }

    @Override
    public boolean hasRunJSBundle() {
        return false;
    }

    @Nullable
    @Override
    public String getSourceURL() {
        return null;
    }

    @Override
    public void invokeCallback(int callbackID, NativeArray arguments) {

    }

    @Override
    public void callFunction(String module, String method, NativeArray arguments) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

    @Override
    public void initialize() {

    }

    @Override
    public ReactQueueConfiguration getReactQueueConfiguration() {
        return mReactQueueConfiguration;
    }

    @Override
    public <T extends JavaScriptModule> T getJSModule(Class<T> jsInterface) {
        return mJSModuleRegistry.getJavaScriptModule(this, jsInterface);
    }

    @Override
    public <T extends NativeModule> boolean hasNativeModule(Class<T> nativeModuleInterface) {
        return mNativeModuleRegistry.hasModule(nativeModuleInterface);
    }

    // This is only ever called with UIManagerModule or CurrentViewerModule.
    @Override
    public <T extends NativeModule> T getNativeModule(Class<T> nativeModuleInterface) {
        return mNativeModuleRegistry.getModule(nativeModuleInterface);
    }

    // This is only used by com.facebook.react.modules.common.ModuleDataCleaner
    @Override
    public Collection<NativeModule> getNativeModules() {
        return mNativeModuleRegistry.getAllModules();
    }

    @Override
    public <T extends JSIModule> T getJSIModule(Class<T> jsiModuleInterface) {
        return null;
    }

    @Override
    public void extendNativeModules(NativeModuleRegistry modules) {

    }

    @Override
    public void addBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener listener) {

    }

    @Override
    public void removeBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener listener) {

    }

    @Override
    public void registerSegment(int segmentId, String path) {

    }

    @Override
    public void setGlobalVariable(String propName, String jsonValue) {

    }

    @Override
    public JavaScriptContextHolder getJavaScriptContextHolder() {
        return null;
    }

    @Override
    public void addJSIModules(List<JSIModuleHolder> jsiModules) {

    }
}
