package com.gotc.actions.conditional;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Pair;
import com.gotc.util.Util;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.support.ValueStack;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class WhileAction implements Action<Object>, Opcodes {

    private boolean preParse = true;
    private Label beginLabel = new Label();
    private Label endLabel;

    @Override
    public boolean run(Context<Object> context) {
        ValueStack stack = context.getValueStack();
        Pair pair = (Pair) stack.peek(stack.size() - 1);
        MethodVisitor visitor = (MethodVisitor) pair.writer;
        DeclarationDictionary dictionary = pair.dictionary;
        if (preParse) {
            String expr = (String) stack.pop();
            visitor.visitLabel(beginLabel);
            int exprPosition = dictionary.getVariableIndex(expr);
            if (exprPosition != -1) {
                visitor.visitVarInsn(ILOAD, exprPosition);
                endLabel = new Label();
                visitor.visitJumpInsn(IFEQ, endLabel);
            } else if (!Util.isNumber(expr)) {
                Util.constructError(context, "Expression expected at " + context.getCurrentIndex());
            }
            dictionary.takeSnapshot();
            preParse = false;
        } else {
            visitor.visitJumpInsn(GOTO, beginLabel);
            if (endLabel != null) {
                visitor.visitLabel(endLabel);
            }
            dictionary.restoreSnapshot();
        }
        return true;
    }
}

