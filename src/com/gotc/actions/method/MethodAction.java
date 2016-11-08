package com.gotc.actions.method;

import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.support.ValueStack;

import java.util.Stack;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class MethodAction implements Action<Object> {

    private boolean preParse = true;

    @Override
    public boolean run(Context<Object> context) {
        if (preParse) {
            preParse = false;
        } else {
            ValueStack<Object> stack = context.getValueStack();
            System.out.println("##### MethodAction run 16 " );
            int size = stack.size();
            boolean nonVoidMethod = (boolean) stack.pop();
            System.out.println("##### MethodAction run 18 nonvoid method " + nonVoidMethod);
            Stack<Object> localStack = new Stack<>();
            while (stack.size() > 2) {
                localStack.push(stack.pop());
            }
            localStack.forEach(item -> System.out.println("##### MethodAction run 23 " + item));
            String methodName = (String) stack.pop();
            System.out.println("##### MethodAction run 17 method name " + methodName);
            System.out.println(stack.isEmpty());
        }
        return true;
    }
}
