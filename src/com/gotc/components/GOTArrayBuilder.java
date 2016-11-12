package com.gotc.components;

import org.parboiled.common.ArrayBuilder;

/**
 * Created by srikaram on 11-Nov-16.
 */
class GOTArrayBuilder<T> extends ArrayBuilder<T> {

    T addAndGet(T val) {
        add(val);
        return val;
    }
}
