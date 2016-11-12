package com.gotc.nodes.arithmetic;

import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class PlusOpNode extends ArithmeticNode {

    public PlusOpNode(StringVar var, StringVar expr) {
        super(var, expr);
    }

    @Override
    int getOp() {
        return IADD;
    }
}
