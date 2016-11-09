package com.gotc.actions.method;

import com.gotc.util.Pair;
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
            Pair pair = (Pair) context.getValueStack().peek(context.getValueStack().size() - 1);
            classWriter = (ClassWriter) pair.writer;
            main = classWriter.visitMethod(
                    ACC_PUBLIC + ACC_STATIC,
                    "main",
                    "([Ljava/lang/String;)V",
                    null,
                    null
            );
            pair.writer = main;
            context.getValueStack().poke(0, pair);
            preParse = false;
        } else {
            System.out.println("##### MainMethodAction run 35 " );
            main.visitInsn(RETURN);
            main.visitMaxs(100, 100);
            main.visitEnd();
            Pair pair = (Pair) context.getValueStack().peek(context.getValueStack().size() - 1);
            pair.writer = classWriter;
            pair.dictionary.clearVariables();
        }
        return true;
    }
}
