package net.leng.maze.util;

import java.time.LocalTime;

public class Logger {
    public static void log(String s) {
        LocalTime time = LocalTime.now();
        int hr = time.getHour();
        int min = time.getMinute();
        int sec = time.getSecond();
        System.out.println("[Log " + hr + ":" + min + ":" + sec + "] " + s);
    }
}
