package com.gotc.actions;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class VariableAssignAction implements Action<Object> {
    @Override
    public boolean run(Context<Object> context) {
        String varName = (String) context.getValueStack().pop(1);
        String value = (String) context.getValueStack().pop();
        System.out.println("##### VariableAssignAction run 12 varname " + varName);
        System.out.println("##### VariableAssignAction run 13 value " );
        //TODO
        return true;
    }
}
