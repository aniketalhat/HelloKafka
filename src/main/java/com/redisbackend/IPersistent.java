package com.redisbackend;

import java.io.Closeable;
/**
 * @author aniket
 *
 */
public interface IPersistent extends Closeable {
    public void setState(byte[] key, Object value);
    public Object getState(byte[] key);
}