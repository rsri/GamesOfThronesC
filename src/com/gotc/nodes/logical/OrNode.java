package com.gotc.nodes.logical;

import com.gotc.nodes.TwoOperandNode;
import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Util;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class OrNode extends TwoOperandNode {

    public OrNode(StringVar var, StringVar expr) {
        super(var, expr);
    }

    @Override
    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
        Label eitherTrue = new Label();
        Label bothFalse = new Label();
        Label finalLabel = new Label();
        int varPosition = dict.getVariableIndex(getVar());
        visitVariable(mVisitor, varPosition, getVar());
        mVisitor.visitJumpInsn(IFNE, eitherTrue);
        visitExpression(mVisitor, getExpr(), dict);
        mVisitor.visitJumpInsn(IFEQ, bothFalse);
        mVisitor.visitLabel(eitherTrue);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitJumpInsn(GOTO, finalLabel);
        mVisitor.visitLabel(bothFalse);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitLabel(finalLabel);
        mVisitor.visitVarInsn(ISTORE, varPosition);
    }
}
