package com.gotc.actions;

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
        MethodVisitor methodVisitor = (MethodVisitor) context.getValueStack().peek(context.getValueStack().size() - 1);
        Object value = context.getValueStack().pop();
        methodVisitor.visitFieldInsn(GETSTATIC, getInternalName(System.class), "out", getDescriptor(PrintStream.class));
        methodVisitor.visitLdcInsn(value.toString());
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(PrintStream.class), "println", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
//        if (value instanceof Long) {
//            System.out.println("##### PrintAction run 14 int " + value);
//        } else {
//            System.out.println("##### PrintAction run 16 string " + value);
//        }
        return true;
    }
}
