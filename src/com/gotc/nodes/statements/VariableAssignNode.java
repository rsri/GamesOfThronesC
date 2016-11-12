package com.gotc.nodes.statements;

import com.gotc.nodes.TwoOperandNode;
import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Util;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class VariableAssignNode extends TwoOperandNode {

    public VariableAssignNode(StringVar var, StringVar expr) {
        super(var, expr);
    }

    @Override
    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
        int position = dict.putVariable(getVar());
        if (position != -1) {
            Util.constructError("Duplicate variable declaration %s", getVar());
        }
        visitExpression(mVisitor, getExpr(), dict);
        mVisitor.visitVarInsn(ISTORE, position);
    }
}