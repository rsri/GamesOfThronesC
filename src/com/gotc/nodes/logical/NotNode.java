package com.gotc.nodes.logical;

import com.gotc.nodes.OneOperandNode;
import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Util;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class NotNode extends OneOperandNode {

    public NotNode(StringVar expr) {
        super(expr);
    }

    @Override
    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
        Label falseLabel = new Label();
        Label finalLabel = new Label();
        String errorMsg = "Variable %s not found";
        int varPosition = dict.getVariableIndex(getExpression());
        if (varPosition != -1) {
            mVisitor.visitVarInsn(ILOAD, varPosition);
            mVisitor.visitJumpInsn(IFNE, falseLabel);
            mVisitor.visitInsn(ICONST_1);
            mVisitor.visitJumpInsn(GOTO, finalLabel);
            mVisitor.visitLabel(falseLabel);
            mVisitor.visitInsn(ICONST_0);
            mVisitor.visitLabel(finalLabel);
            mVisitor.visitIntInsn(ISTORE, varPosition);
        } else {
            Util.constructError(errorMsg, getExpression());
        }
    }
}