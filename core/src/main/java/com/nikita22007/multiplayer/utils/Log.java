package com.nikita22007.multiplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static void w(String s, Object... params) {
    }

    public static void wtf(String s, Object... params) {
    }

    public static void e(String s, Object... params) {
    }

    public static void i(String s, Object... params) {
    }

    private static void print(String logLevel, String s, Object... params)
    {
        System.out.println(String.format(( new SimpleDateFormat("HH:MM:SS").format(new Date())), logLevel, String.format(s,params)));
    }
}
