package com.gotc.nodes;

import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 11-Nov-16.
 */
public abstract class OneOperandNode extends GOTNode {
    private final String expression;

    public OneOperandNode(StringVar expression) {
        this.expression = expression.get();
    }

    public String getExpression() {
        return expression;
    }
}
