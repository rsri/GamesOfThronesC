package com.gotc.components;

import com.gotc.nodes.GOTNode;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.support.Checks;
import org.parboiled.support.ValueStack;

import java.util.Stack;

/**
 * Created by srikaram on 06-Nov-16.
 */
class GOTBaseParser extends BaseParser<GOTNode> {

    boolean addAll(GOTNode node) {
        Context<GOTNode> context = getContext();
        check(context);
        ValueStack<GOTNode> stack = context.getValueStack();
        while (!stack.isEmpty()) {
            node.addChild(stack.pop());
        }
        return push(node);
    }

    Action<GOTNode> create = context -> {
        System.out.println("##### GOTBaseParser  31 " );
        return true;
    };

    private void check(Context<GOTNode> context) {
        Checks.ensure(context != null && context.getMatcher() != null,
                "Illegal rule definition: Unwrapped action expression!");
    }
}
