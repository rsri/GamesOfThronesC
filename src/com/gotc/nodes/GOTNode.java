package com.gotc.nodes;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Util;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.parboiled.trees.MutableTreeNodeImpl;

/**
 * Created by srikaram on 10-Nov-16.
 */
public abstract class GOTNode extends MutableTreeNodeImpl<GOTNode> implements Opcodes {

    public GOTNode addChild(GOTNode child) {
        int index = getChildren().size();
        addChild(index, child);
        return child;
    }

    public void build() {
    }

    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {}

    public void build(ClassWriter cVisitor, DeclarationDictionary dict) {}

    protected void visitExpression(MethodVisitor mVisitor, String expression, DeclarationDictionary dict) {
        int exprPosition = dict.getVariableIndex(expression);
        int intVal = Util.isNumber(expression) ? Integer.parseInt(expression) : -1;
        String errorMsg = "Variable %s not found";
        if (intVal >= 0) {
            if (intVal == 0) {
                mVisitor.visitInsn(ICONST_0);
            } else if (intVal == 1) {
                mVisitor.visitInsn(ICONST_1);
            } else {
                mVisitor.visitIntInsn(BIPUSH, intVal);
            }
        } else if (exprPosition != -1) {
            mVisitor.visitVarInsn(ILOAD, exprPosition);
        } else {
            Util.constructError(errorMsg, expression);
        }
    }

    public void iterate(MethodVisitor mVisitor, DeclarationDictionary dict) {
        getChildren().forEach(child -> child.build(mVisitor, dict));
    }

    protected void visitVariable(MethodVisitor mVisitor, int varIndex, String var) {
        String errorMsg = "Variable %s not found";
        if (varIndex != -1) {
            mVisitor.visitVarInsn(ILOAD, varIndex);
        } else {
            Util.constructError(errorMsg, var);
        }
    }

}
