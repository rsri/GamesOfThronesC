package com.gotc.actions.method;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Pair;
import com.gotc.util.Util;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.support.ValueStack;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class ReturnAction implements Action<Object>, Opcodes {

    @Override
    public boolean run(Context<Object> context) {
        System.out.println("##### ReturnAction run 13 ");
        ValueStack stack = context.getValueStack();
        Pair pair = (Pair) stack.peek(stack.size() - 1);
        MethodVisitor visitor = (MethodVisitor) pair.writer;
        DeclarationDictionary dictionary = pair.dictionary;
        if (stack.size() == 1) {
            if (dictionary.getCurrentMethod().isNonVoid()) {
                Util.constructError(context,
                        "Method " + dictionary.getCurrentMethod().getMethodName() + " should return value");
            }
            visitor.visitInsn(RETURN);
        } else {
            if (!dictionary.getCurrentMethod().isNonVoid()) {
                Util.constructError(context,
                        "Method " + dictionary.getCurrentMethod().getMethodName() + " shouldn't return value");
            }
            String expression = (String) stack.pop();
            int exprPosition = dictionary.getVariableIndex(expression);
            int intVal = Util.isNumber(expression) ? Integer.parseInt(expression) : -1;
            String errorMsg = "Variable not found : ";
            if (intVal >= 0) {
                visitor.visitIntInsn(BIPUSH, intVal);
            } else if (exprPosition != -1) {
                visitor.visitVarInsn(ILOAD, exprPosition);
            } else {
                Util.constructError(context, errorMsg + expression);
            }
            visitor.visitInsn(IRETURN);
        }
        return true;
    }
}
