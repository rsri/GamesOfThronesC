package com.gotc.nodes.conditional;

import com.gotc.nodes.TwoOperandNode;
import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Util;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 12-Nov-16.
 */
abstract class ConditionalNode extends TwoOperandNode {
    
    ConditionalNode(StringVar var, StringVar expr) {
        super(var, expr);
    }

    @Override
    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
        Label falseLabel = new Label();
        Label finalLabel = new Label();
        int varPosition = dict.getVariableIndex(getVar());
        visitVariable(mVisitor, varPosition, getVar());
        visitExpression(mVisitor, getExpr(), dict);
        mVisitor.visitJumpInsn(getOp(), falseLabel);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitJumpInsn(GOTO, finalLabel);
        mVisitor.visitLabel(falseLabel);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitLabel(finalLabel);
        mVisitor.visitVarInsn(ISTORE, varPosition);
    }

    abstract int getOp();
}
