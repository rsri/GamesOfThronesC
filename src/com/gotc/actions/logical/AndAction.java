package com.gotc.actions.logical;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class AndAction implements Action<Object> {
    @Override
    public boolean run(Context<Object> context) {
        System.out.println("##### AndAction run 12 varname 1 " + context.getValueStack().pop(1));
        System.out.println("##### AndAction run 13 varname 2 " + context.getValueStack().pop());
        return true;
    }
}
