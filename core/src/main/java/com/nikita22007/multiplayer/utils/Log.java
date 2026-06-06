package com.nikita22007.multiplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static void w(String tag, String s, Object... params) {
        print("W", tag,  s, params);
    }

    public static void wtf(String tag, String s, Object... params) {
        print("WTF", tag,  s, params);
    }

    public static void e(String tag, String s, Object... params) {
        print("E", tag,  s, params);
    }

    public static void i(String tag, String s, Object... params) {
        print("I", tag, s, params);
    }

    private static void print(String logLevel, String tag, String s, Object... params)
    {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String message = (params != null && params.length > 0) ? String.format(s, params) : s;
        System.out.printf("[%s] [%s] %s: %s", time, logLevel, tag, message);
    }
}
