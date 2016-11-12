package com.gotc.nodes.conditional;

import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class EqualToNode extends ConditionalNode {

    public EqualToNode(StringVar var, StringVar expr) {
        super(var, expr);
    }

    @Override
    int getOp() {
        return IF_ICMPNE;
    }


}
