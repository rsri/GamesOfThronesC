package com.gotc.nodes.arithmetic;

import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class MinusOpNode extends ArithmeticNode {

    public MinusOpNode(StringVar var, StringVar expr) {
        super(var, expr);
    }

    @Override
    int getOp() {
        return ISUB;
    }
}
