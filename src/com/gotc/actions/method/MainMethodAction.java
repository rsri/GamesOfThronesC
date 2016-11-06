package com.gotc.actions.method;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class MainMethodAction implements Action<Object> {

    @Override
    public boolean run(Context<Object> context) {
        System.out.println("##### MainMethodAction run 13 ");
        return true;
    }
}
