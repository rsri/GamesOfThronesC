package com.gotc.actions;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class PrintAction implements Action<Object> {
    @Override
    public boolean run(Context<Object> context) {
        Object value = context.getValueStack().pop();
        if (value instanceof Long) {
            System.out.println("##### PrintAction run 14 int " + value);
        } else {
            System.out.println("##### PrintAction run 16 string " + value);
        }
        return true;
    }
}
