package com.gotc.nodes;

import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 11-Nov-16.
 */
public abstract class TwoOperandNode extends GOTNode {

    private final String var;
    private final String expr;

    public TwoOperandNode(StringVar var, StringVar expr) {
        this.var = var.get();
        this.expr = expr.get();
    }

    protected String getVar() {
        return var;
    }

    protected String getExpr() {
        return expr;
    }
}
