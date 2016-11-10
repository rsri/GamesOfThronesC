package com.gotc.actions.method;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Pair;
import com.gotc.util.Util;
import org.objectweb.asm.ClassWriter;
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
public class MethodAction implements Action<Object>, Opcodes {

    private boolean preParse = true;
    private ClassWriter classWriter;
    private MethodVisitor methodVisitor;

    @Override
    public boolean run(Context<Object> context) {
        ValueStack<Object> stack = context.getValueStack();
        Pair pair = (Pair) stack.peek(stack.size() - 1);
        DeclarationDictionary dictionary = pair.dictionary;
        if (preParse) {
            classWriter = (ClassWriter) pair.writer;
            boolean nonVoidMethod = (boolean) stack.pop();
            Stack<String> localStack = new Stack<>();
            while (stack.size() > 2) {
                localStack.push((String) stack.pop());
            }
            String methodName = (String) stack.pop();
            dictionary.addMethod(methodName, localStack.size(), nonVoidMethod);
            dictionary.setCurrentMethod(dictionary.getMethod(methodName, localStack.size()));
            methodVisitor = classWriter.visitMethod(
                    ACC_PRIVATE + ACC_STATIC,
                    methodName,
                    buildMethodSignature(localStack.size(), nonVoidMethod),
                    null,
                    null);
            localStack.forEach(varName -> {
                if (dictionary.putVariable(varName) == -1) {
                    Util.constructError(context, "Duplicate variable declaration : " + varName);
                }
            });
            pair.writer = methodVisitor;
            preParse = false;
        } else {
            if (dictionary.getCurrentMethod().isNonVoid()) {
                methodVisitor.visitInsn(ICONST_0);
                methodVisitor.visitInsn(IRETURN);
            } else {
                methodVisitor.visitInsn(RETURN);
            }
            methodVisitor.visitMaxs(100, 100);
            methodVisitor.visitEnd();
            pair.writer = classWriter;
            dictionary.clearVariables();
        }
        return true;
    }
}
