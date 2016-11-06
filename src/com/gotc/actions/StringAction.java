package com.gotc.actions;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class StringAction implements Action<Object> {
    @Override
    public boolean run(Context<Object> context) {
        return true;
    }
}
