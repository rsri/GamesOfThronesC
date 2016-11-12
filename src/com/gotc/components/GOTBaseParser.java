package com.gotc.components;

import com.gotc.nodes.GOTNode;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.IntArrayStack;
import org.parboiled.support.Checks;
import org.parboiled.support.ValueStack;

import java.util.Stack;

/**
 * Created by srikaram on 06-Nov-16.
 */
class GOTBaseParser extends BaseParser<GOTNode> {

    private final IntArrayStack intStack = new IntArrayStack();

    boolean addAll(GOTNode node) {
        Context<GOTNode> context = getContext();
        check(context);
        ValueStack<GOTNode> stack = context.getValueStack();
        int toBeLeftCount = intStack.isEmpty() ? -1 : intStack.pop();
        while (true) {
            if (toBeLeftCount != -1) {
                if (stack.size() <= toBeLeftCount) {
                    break;
                }
            } else if (stack.isEmpty()) {
                break;
            }
            GOTNode nn = stack.pop();
            node.addChild(nn);
        }
        return push(node);
    }

    Action<GOTNode> pushSize = context -> {
        intStack.push(context.getValueStack().size());
        return true;
    };

    private void check(Context<GOTNode> context) {
        Checks.ensure(context != null && context.getMatcher() != null,
                "Illegal rule definition: Unwrapped action expression!");
    }
}
