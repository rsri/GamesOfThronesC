package com.gotc.actions.method;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Pair;
import org.objectweb.asm.Opcodes;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.errors.BasicParseError;
import org.parboiled.support.ValueStack;

/**
 * Created by srikaram on 08-Nov-16.
 */
public class MethodPrototypeAction implements Action<Object>, Opcodes {
    @Override
    public boolean run(Context<Object> context) {
        ValueStack stack = context.getValueStack();
        boolean nonVoidMethod = ((int) stack.pop()) == 1;
        int argsCount = (int) stack.pop();
        String methodName = (String) stack.pop();
        System.out.println("##### MethodPrototypeAction run 13 methodname " + methodName);
        System.out.println("##### MethodPrototypeAction run 14 count " + argsCount);
        DeclarationDictionary dictionary = ((Pair) stack.peek(stack.size() - 1)).dictionary;
        if (!dictionary.addMethodDescription(methodName, argsCount, nonVoidMethod)) {
            BasicParseError parseError = new BasicParseError(context.getInputBuffer(), context.getCurrentIndex(),
                    "Duplicate method declaration");
            context.getParseErrors().add(parseError);
            return false;
        }
        return true;
    }
}
