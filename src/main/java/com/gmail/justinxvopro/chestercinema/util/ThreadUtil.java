package com.gmail.justinxvopro.chestercinema.util;

public class ThreadUtil {
    public static void delay(long delayInMs) {
	try {
	    Thread.sleep(delayInMs);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }
}
