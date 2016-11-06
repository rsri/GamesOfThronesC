package com.gotc.actions.method;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class MethodCallAction implements Action<Object> {

    @Override
    public boolean run(Context<Object> context) {
        while (!context.getValueStack().isEmpty()) {
            System.out.println("##### MethodCallAction run 14 " + context.getValueStack().pop());
        }
        return true;
    }
}
