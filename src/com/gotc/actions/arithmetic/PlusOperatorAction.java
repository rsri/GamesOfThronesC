package com.gotc.actions.arithmetic;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class PlusOperatorAction implements Action<Object> {
    @Override
    public boolean run(Context<Object> context) {
        System.out.println("##### PlusOperatorAction run 12 varname 1 " + context.getValueStack().pop(1));
        System.out.println("##### PlusOperatorAction run 13 varname 2 " + context.getValueStack().pop());
        return true;
    }
}
