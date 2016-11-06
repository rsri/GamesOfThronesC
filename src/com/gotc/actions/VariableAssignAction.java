package com.gotc.actions;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class VariableAssignAction implements Action<Object> {
    @Override
    public boolean run(Context<Object> context) {
        System.out.println("##### VariableAssignAction run 12 varname " + context.getValueStack().pop(1));
        System.out.println("##### VariableAssignAction run 13 value " + context.getValueStack().pop());
        return true;
    }
}
