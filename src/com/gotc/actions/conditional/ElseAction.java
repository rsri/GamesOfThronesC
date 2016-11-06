package com.gotc.actions.conditional;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class ElseAction implements Action<Object> {
    @Override
    public boolean run(Context<Object> context) {
        System.out.println("##### ElseAction run 12 ");
        return true;
    }
}
