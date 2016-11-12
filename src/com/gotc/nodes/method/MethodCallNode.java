package com.gotc.nodes.method;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.DeclarationDictionary.Method;
import com.gotc.util.Util;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;

import java.util.Arrays;

import static com.gotc.util.Util.buildMethodSignature;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class MethodCallNode extends AbstractMethodNode {

    public MethodCallNode(StringVar methodName, StringVar[] args, StringVar resultingVar) {
        super(methodName, args, resultingVar);
    }

    private String getResultingVar() {
        return getThirdVar();
    }

    @Override
    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
        Method method = dict.getMethod(getMethodName(), getArgsCount());
        if (method == null) {
            Util.constructError("Method %s not found", getMethodName());
        }
        Arrays.spliterator(getArgs()).forEachRemaining(expression -> visitExpression(mVisitor, expression, dict));
        mVisitor.visitMethodInsn(INVOKESTATIC, dict.getClassName(), getMethodName(),
                buildMethodSignature(method.getArgsCount(), method.isNonVoid()), false);
        if (getResultingVar() != null) {
            if (!method.isNonVoid()) {
                Util.constructError("Attempt to save return value from a void method call %s", getMethodName());
            }
            int varIndex = dict.getVariableIndex(getResultingVar());
            if (varIndex == -1) {
                Util.constructError("Variable %s not found ", getResultingVar());
            }
            mVisitor.visitIntInsn(ISTORE, varIndex);
        } else {
            if (method.isNonVoid()) {
                mVisitor.visitInsn(POP);
            }
        }
    }
}

