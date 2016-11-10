package com.gotc.actions.method;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Pair;
import com.gotc.util.Util;
import org.objectweb.asm.Opcodes;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.support.ValueStack;

/**
 * Created by srikaram on 08-Nov-16.
 */
public class MethodPrototypeAction implements Action<Object>, Opcodes {
    @Override
    public boolean run(Context<Object> context) {
        ValueStack stack = context.getValueStack();
        boolean nonVoidMethod = (boolean) stack.pop();
        int argsCount = Integer.parseInt((String) stack.pop());
        String methodName = (String) stack.pop();
        DeclarationDictionary dictionary = ((Pair) stack.peek(stack.size() - 1)).dictionary;
        if (!dictionary.addMethod(methodName, argsCount, nonVoidMethod)) {
            Util.constructError(context, "Duplicate method declaration");
        }
        return true;
    }
}
