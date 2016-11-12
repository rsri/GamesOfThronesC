package com.gotc.nodes.conditional;

import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class LesserThanNode extends ConditionalNode {

    public LesserThanNode(StringVar var, StringVar expr) {
        super(var, expr);
    }

    @Override
    int getOp() {
        return IF_ICMPGE;
    }
}
