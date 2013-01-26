package com.sforce.bulk;


/**
 * This class represents
 * <p/>
 * User: mcheenath
 * Date: Dec 14, 2010
 */
public class StreamException extends Exception {
    private static final long serialVersionUID = 1L;

    public StreamException(String message) {
        super(message);
    }

    public StreamException(String s, Throwable e) {
        super(s, e);
    }
}
