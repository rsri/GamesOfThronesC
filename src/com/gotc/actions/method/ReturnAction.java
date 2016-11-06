package com.gotc.actions.method;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class ReturnAction implements Action<Object> {

    @Override
    public boolean run(Context<Object> context) {
        System.out.println("##### ReturnAction run 13 ");
        return true;
    }
}
