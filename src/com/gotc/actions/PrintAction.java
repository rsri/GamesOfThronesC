package com.gotc.actions;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Pair;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.parboiled.Action;
import org.parboiled.Context;

import java.io.PrintStream;

import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class PrintAction implements Action<Object>, Opcodes {
    @Override
    public boolean run(Context<Object> context) {
        Pair pair = (Pair) context.getValueStack().peek(context.getValueStack().size() - 1);
        MethodVisitor methodVisitor = (MethodVisitor) pair.writer;
        Object value = context.getValueStack().pop();
        methodVisitor.visitFieldInsn(GETSTATIC, getInternalName(System.class), "out", getDescriptor(PrintStream.class));
        DeclarationDictionary dictionary = pair.dictionary;
        int position = dictionary.getVariableIndex(value.toString());
        if (position == -1) {
            methodVisitor.visitLdcInsn(value.toString());
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(PrintStream.class), "println", "(Ljava/lang/String;)V", false);
        } else {
            methodVisitor.visitVarInsn(ILOAD, position);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(PrintStream.class), "println", "(I)V", false);
        }
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
        return true;
    }
}
