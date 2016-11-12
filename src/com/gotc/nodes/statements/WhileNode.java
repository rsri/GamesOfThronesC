package com.gotc.nodes.statements;

import com.gotc.nodes.OneOperandNode;
import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Util;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class WhileNode extends OneOperandNode {

    private final Label beginLabel = new Label();
    private Label endLabel;

    public WhileNode(StringVar expr) {
        super(expr);
    }

    @Override
    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
        mVisitor.visitLabel(beginLabel);
        int exprPosition = dict.getVariableIndex(getExpression());
        if (exprPosition != -1) {
            mVisitor.visitVarInsn(ILOAD, exprPosition);
            endLabel = new Label();
            mVisitor.visitJumpInsn(IFEQ, endLabel);
        } else if (!Util.isNumber(getExpression())) {
            Util.constructError("Expression expected in while in %s method",
                    dict.getCurrentMethod().getMethodName());
        }
        dict.takeSnapshot();
        iterate(mVisitor, dict);
        mVisitor.visitJumpInsn(GOTO, beginLabel);
        if (endLabel != null) {
            mVisitor.visitLabel(endLabel);
        }
        dict.restoreSnapshot();
    }
}