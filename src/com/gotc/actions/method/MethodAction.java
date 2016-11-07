package com.gotc.actions.method;

import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.support.ValueStack;

import java.util.Stack;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class MethodAction implements Action<Object> {
    @Override
    public boolean run(Context<Object> context) {
        ValueStack<Object> stack = context.getValueStack();
        System.out.println("##### MethodAction run 16 " );
//        int size = stack.size();
//        System.out.println("##### MethodAction run 17 method name " + stack.pop(size - 1));
//        System.out.println("##### MethodAction run 18 nonvoid method " + stack.pop());
//        Stack<Object> localStack = new Stack<>();
//        while (stack.size() > 1) {
//            localStack.push(stack.pop());
//        }
//        localStack.forEach(item -> System.out.println("##### MethodAction run 23 " + item));
//        System.out.println(stack.isEmpty());
        return true;
    }
}
