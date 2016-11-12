package com.gotc.nodes.statements;

import com.gotc.nodes.OneOperandNode;
import com.gotc.util.DeclarationDictionary;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

import java.io.PrintStream;

import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class PrintNode extends OneOperandNode {

    public PrintNode(StringVar expression) {
        super(expression);
    }

    @Override
    public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
        mVisitor.visitFieldInsn(GETSTATIC, getInternalName(System.class), "out",
                getDescriptor(PrintStream.class));
        int position = dict.getVariableIndex(getExpression());
        if (position != -1) {
            mVisitor.visitLdcInsn(getExpression());
            mVisitor.visitMethodInsn(INVOKEVIRTUAL,
                    getInternalName(PrintStream.class), "println",
                    "(Ljava/lang/String;)V", false);
        } else {
            mVisitor.visitVarInsn(ILOAD, position);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL,
                    getInternalName(PrintStream.class), "println",
                    "(I)V", false);
        }
    }
}
