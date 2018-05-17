package com.facebook.react;

/**
 * Created by jesseruder on 5/17/18.
 */

public class DuktapeRN {

    public interface DuktapeRNExecutor {
        void execute(String js);
    }

    public static DuktapeRNExecutor executor;

}
