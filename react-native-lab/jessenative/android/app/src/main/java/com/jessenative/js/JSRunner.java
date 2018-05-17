package com.jessenative.js;

import android.content.Context;
import android.util.Log;

import com.facebook.react.DuktapeRN;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.uimanager.UIManagerModule;
import com.squareup.duktape.Duktape;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSRunner {

    interface JSInterface {
        void createView(int tag, String className, String props);
        void updateView(int tag, String className, String props);
        void setChildren(int viewTag, String childrenTags);
        void onBatchComplete();
    }

    private static JSRunner sInstance;

    public static JSRunner getInstance(Context context, UIManagerModule module) {
        if (sInstance == null) {
            sInstance = new JSRunner(context, module);
        }

        return sInstance;
    }

    private final Context mContext;
    private final Duktape mDuktape;
    private final UIManagerModule mUIManagerModule;

    private JSRunner(Context context, UIManagerModule module) {
        mContext = context;
        mDuktape = Duktape.create();
        mUIManagerModule = module;

        mDuktape.set("UI", JSInterface.class, new JSInterface() {

            @Override
            public void createView(int tag, String className, String props) {
                mUIManagerModule.createView(tag, className, 1, JSONBundleConverter.JSONStringToReadableMap(props));
            }

            @Override
            public void updateView(int tag, String className, String props) {
                mUIManagerModule.updateView(tag, className, JSONBundleConverter.JSONStringToReadableMap(props));
            }

            @Override
            public void setChildren(int viewTag, String childrenTags) {
                try {
                    WritableArray writableArray = Arguments.createArray();
                    JSONArray jsonArray = new JSONArray(childrenTags);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        writableArray.pushInt(jsonArray.getInt(i));
                    }
                    mUIManagerModule.setChildren(viewTag, writableArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onBatchComplete() {
                mUIManagerModule.onBatchComplete();
            }
        });

        DuktapeRN.executor = new DuktapeRN.DuktapeRNExecutor() {
            @Override
            public void execute(String js) {
                mDuktape.evaluate(js);
            }
        };
    }

    public void run() {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = mContext.getAssets().open("bundle.js");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str=in.readLine()) != null) {
                buf.append(str + "\n");
            }

            in.close();

            mDuktape.evaluate(buf.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        mDuktape.close();
    }
}
