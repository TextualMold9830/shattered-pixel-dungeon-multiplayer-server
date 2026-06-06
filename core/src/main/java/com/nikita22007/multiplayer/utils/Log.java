package com.nikita22007.multiplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static void w(String s, Object... params) {
        print("W", s, params);
    }

    public static void wtf(String s, Object... params) {
        print("WTF", s, params);
    }

    public static void e(String s, Object... params) {
        print("E", s, params);
    }

    public static void i(String s, Object... params) {
        print("I", s, params);
    }

    private static void print(String logLevel, String s, Object... params)
    {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String message = (params != null && params.length > 0) ? String.format(s, params) : s;
        System.out.printf("[%s] [%s] %s:%n", time, logLevel, message);
    }
}
