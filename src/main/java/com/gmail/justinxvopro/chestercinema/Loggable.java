package com.gmail.justinxvopro.chestercinema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Loggable {
    default Logger logger() {
	return logger(getClass());
    }
    
    public static Logger logger(Class<?> l) {
	return LoggerFactory.getLogger(l);
    }
}
