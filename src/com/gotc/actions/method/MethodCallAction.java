package com.gotc.actions.method;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.DeclarationDictionary.Method;
import com.gotc.util.Pair;
import com.gotc.util.Util;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.support.ValueStack;

import java.util.Stack;

import static com.gotc.util.Util.buildMethodSignature;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class MethodCallAction implements Action<Object>, Opcodes {

    private boolean preParse = true;
    private Method method;

    @Override
    public boolean run(Context<Object> context) {
        ValueStack stack = context.getValueStack();
        Pair pair = (Pair) stack.peek(stack.size() - 1);
        MethodVisitor visitor = (MethodVisitor) pair.writer;
        DeclarationDictionary dictionary = pair.dictionary;
        if (preParse) {
            Stack<String> localStack = new Stack<>();
            while (stack.size() > 2) {
                localStack.push((String) stack.pop());
            }
            String methodName = (String) stack.pop();
            method = dictionary.getMethod(methodName, localStack.size());
            String methodError = "Method (%s, %d) not found, declare a method prototype or " +
                    "define it above the calling method.";
            if (method == null) {
                Util.constructError(context, String.format(methodError, methodName, localStack.size()));
            }
            localStack.forEach(expression -> {
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
            });
            visitor.visitMethodInsn(INVOKESTATIC, dictionary.getClassName(),
                    methodName, buildMethodSignature(localStack.size(), method.isNonVoid()), false);
            preParse = false;
        } else {
            if (stack.size() == 2) {
                if (!method.isNonVoid()) {
                    Util.constructError(context, "Attempt to save return value from a void method call");
                }
                String var = (String) stack.pop();
                int varIndex = dictionary.getVariableIndex(var);
                if (varIndex == -1) {
                    Util.constructError(context, "Variable not found : " + var);
                }
                visitor.visitIntInsn(ISTORE, varIndex);
            } else {
                if (method.isNonVoid()) {
                    visitor.visitInsn(POP);
                }
            }
        }
        return true;
    }


}
