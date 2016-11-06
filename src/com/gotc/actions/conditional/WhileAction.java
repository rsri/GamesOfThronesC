package com.gotc.actions.conditional;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class WhileAction implements Action<Object> {
    @Override
    public boolean run(Context<Object> context) {
        System.out.println("##### WhileAction run 12 " + context.getValueStack().pop());
        return true;
    }
}

