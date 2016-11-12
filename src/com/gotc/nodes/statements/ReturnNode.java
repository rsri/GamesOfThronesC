package com.gotc.nodes.statements;

import com.gotc.nodes.OneOperandNode;
import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Util;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class ReturnNode extends OneOperandNode {

    public ReturnNode(StringVar expr) {
        super(expr);
    }

    @Override
    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
        if (getExpression() == null) {
            if (dict.getCurrentMethod().isNonVoid()) {
                Util.constructError("Method %s should return a value",
                        dict.getCurrentMethod().getMethodName());
            }
            mVisitor.visitInsn(RETURN);
        } else {
            if (!dict.getCurrentMethod().isNonVoid()) {
                Util.constructError("Method %s shouldn't return a value",
                        dict.getCurrentMethod().getMethodName());
            }
            visitExpression(mVisitor, getExpression(), dict);
            mVisitor.visitInsn(IRETURN);
        }
    }
}