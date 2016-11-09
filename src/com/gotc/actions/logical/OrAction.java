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
public class OrAction implements Action<Object>, Opcodes {
    @Override
    public boolean run(Context<Object> context) {
        ValueStack stack = context.getValueStack();
        String var2 = (String) stack.pop();
        String var1 = (String) stack.pop();
        Pair pair = (Pair) stack.peek();
        MethodVisitor visitor = (MethodVisitor) pair.writer;
        DeclarationDictionary dictionary = pair.dictionary;
        Label eitherTrue = new Label();
        Label bothFalse = new Label();
        Label finalLabel = new Label();
        String errorMsg = "Variable not found : ";
        int varPosition = dictionary.getVariableIndex(var1);
        if (varPosition != -1) {
            visitor.visitVarInsn(ILOAD, varPosition);
        } else {
            Util.constructError(context, errorMsg + var1);
        }
        int exprPosition = dictionary.getVariableIndex(var2);
        int intVal = Util.isNumber(var2) ? Integer.parseInt(var2) : -1;
        if (intVal >= 0) {
            visitor.visitIntInsn(BIPUSH, intVal);
        } else if (exprPosition != -1) {
            visitor.visitVarInsn(ILOAD, exprPosition);
        } else {
            Util.constructError(context, errorMsg + var2);
        }
        visitor.visitJumpInsn(IFEQ, bothFalse);
        visitor.visitLabel(eitherTrue);
        visitor.visitInsn(ICONST_1);
        visitor.visitJumpInsn(GOTO, finalLabel);
        visitor.visitLabel(bothFalse);
        visitor.visitInsn(ICONST_0);
        visitor.visitLabel(finalLabel);
        visitor.visitVarInsn(ISTORE, varPosition);
        return true;
    }

}
