package com.gotc.actions.method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class MainMethodAction implements Action<Object>, Opcodes {

    private boolean preParse = true;

    private ClassWriter classWriter;

    private MethodVisitor main;

    @Override
    public boolean run(Context<Object> context) {
        if (preParse) {
            System.out.println("##### MainMethodAction run 24 " );
            classWriter = (ClassWriter) context.getValueStack().peek(context.getValueStack().size() - 1);
            main = classWriter.visitMethod(
                    ACC_PUBLIC + ACC_STATIC,
                    "main",
                    "([Ljava/lang/String;)V",
                    null,
                    null
            );
            context.getValueStack().poke(0, main);
            preParse = false;
        } else {
            System.out.println("##### MainMethodAction run 35 " );
            main.visitEnd();
            context.getValueStack().poke(0, classWriter);
        }
        return true;
    }
}
