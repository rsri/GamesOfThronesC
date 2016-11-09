package com.gotc.actions.logical;

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
public class NotAction implements Action<Object>, Opcodes {
    @Override
    public boolean run(Context<Object> context) {
        ValueStack stack = context.getValueStack();
        String var = (String) stack.pop();
        Pair pair = (Pair) stack.peek();
        MethodVisitor visitor = (MethodVisitor) pair.writer;
        DeclarationDictionary dictionary = pair.dictionary;
        Label falseLabel = new Label();
        Label finalLabel = new Label();
        String errorMsg = "Variable not found : ";
        int varPosition = dictionary.getVariableIndex(var);
        if (varPosition != -1) {
            visitor.visitVarInsn(ILOAD, varPosition);
            visitor.visitJumpInsn(IFNE, falseLabel);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, finalLabel);
            visitor.visitLabel(falseLabel);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(finalLabel);
            visitor.visitIntInsn(ISTORE, varPosition);
        } else {
            Util.constructError(context, errorMsg + var);
        }
        return true;
    }
}
