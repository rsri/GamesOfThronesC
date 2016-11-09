package com.gotc.actions.conditional;

import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class LesserThanAction extends ConditionalAction {
    @Override
    public boolean run(Context<Object> context) {
        writeOperation(context, IF_ICMPGE);
        return true;
    }
}

