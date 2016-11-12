package com.gotc.nodes.arithmetic;

import com.gotc.nodes.TwoOperandNode;
import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Util;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 12-Nov-16.
 */
abstract class ArithmeticNode extends TwoOperandNode {

    ArithmeticNode(StringVar var, StringVar expr) {
        super(var, expr);
    }

    @Override
    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
        int varPosition = dict.getVariableIndex(getVar());
        visitVariable(mVisitor, varPosition, getVar());
        visitExpression(mVisitor, getExpr(), dict);
        mVisitor.visitInsn(getOp());
        mVisitor.visitVarInsn(ISTORE, varPosition);
    }

    abstract int getOp();
}
