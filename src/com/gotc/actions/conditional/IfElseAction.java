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
public class IfElseAction implements Opcodes {

    private boolean ifPreParse = true;
    private boolean elsePreParse = true;

    private Label ifEnd = new Label();
    private Label elseBegin = new Label();

    private void runIf(Context<Object> context) {
        ValueStack stack = context.getValueStack();
        Pair pair = (Pair) stack.peek(stack.size() - 1);
        MethodVisitor visitor = (MethodVisitor) pair.writer;
        DeclarationDictionary dictionary = pair.dictionary;
        if (ifPreParse) {
            String expr = (String) stack.pop();
            int exprPosition = dictionary.getVariableIndex(expr);
            if (exprPosition != -1) {
                visitor.visitVarInsn(ILOAD, exprPosition);
            } else if (Util.isNumber(expr)) {
                visitor.visitIntInsn(BIPUSH, Integer.parseInt(expr));
            } else {
                Util.constructError(context, "Expression expected at " + context.getCurrentIndex());
            }
            visitor.visitJumpInsn(IFEQ, elseBegin);
            dictionary.takeSnapshot();
            ifPreParse = false;
        } else {
            visitor.visitLabel(ifEnd);
            dictionary.restoreSnapshot();
        }
    }

    private void runElse(Context<Object> context) {
        ValueStack stack = context.getValueStack();
        Pair pair = (Pair) stack.peek(stack.size() - 1);
        MethodVisitor visitor = (MethodVisitor) pair.writer;
        DeclarationDictionary dictionary = pair.dictionary;
        if (elsePreParse) {
            dictionary.restoreSnapshot();
            visitor.visitJumpInsn(GOTO, ifEnd);
            visitor.visitLabel(elseBegin);
            dictionary.takeSnapshot();
            elsePreParse = false;
        }
    }

    public Action<Object> ifAction() {
        return ifAction;
    }

    public Action<Object> elseAction() {
        return elseAction;
    }

    private Action<Object> ifAction = context -> {
        runIf(context);
        return true;
    };

    private Action<Object> elseAction = context -> {
        runElse(context);
        return true;
    };
}

