package com.gotc.actions.arithmetic;

import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class DivideOperatorAction extends ArithmeticOperation {
    @Override
    public boolean run(Context<Object> context) {
        return writeOperation(context, IDIV);
    }
}
