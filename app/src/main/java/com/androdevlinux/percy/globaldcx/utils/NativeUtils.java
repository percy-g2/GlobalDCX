package com.androdevlinux.percy.globaldcx.utils;

/**
 * Created by percy on 22/11/17.
 */

public class NativeUtils {

    static {
        System.loadLibrary("globaldcx-jni");
    }
    public static native String getBitfinexBaseUrl();
}
