package com.facebook.react;

import android.view.View;

/**
 * Created by jesseruder on 5/17/18.
 */

public class DuktapeRN {

    public interface DuktapeRNExecutor {
        void execute(String js);
        void registerView(int tag, View view);
    }

    public static DuktapeRNExecutor executor;

}
