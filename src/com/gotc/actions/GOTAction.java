package com.gotc.actions;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Pair;
import com.gotc.util.Util;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.parboiled.Action;
import org.parboiled.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by srikaram on 07-Nov-16.
 */
public class GOTAction implements Action<Object>, Opcodes {

    private boolean preParse = true;

    private final String fileName;
    private ClassWriter classWriter;

    public GOTAction(String fileName) {
        this.fileName = Util.capitalize(Util.getBaseName(fileName));
    }

    @Override
    public boolean run(Context<Object> context) {
        if (preParse) {
            System.out.println("##### GOTAction run 33 "  );
            classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classWriter.visit(V1_6,
                    ACC_PUBLIC,
                    fileName,
                    null,
                    getInternalName(Object.class),
                    null);
            MethodVisitor constructor = classWriter.visitMethod(
                    ACC_PUBLIC,
                    "<init>",
                    "()V",
                    null,
                    null);
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitMethodInsn(INVOKESPECIAL, getInternalName(Object.class), "<init>", "()V", false);
            constructor.visitInsn(RETURN);
            constructor.visitMaxs(1, 1);
            constructor.visitEnd();
            context.getValueStack().push(0, Pair.create(classWriter, new DeclarationDictionary()));
            preParse = false;
        } else {
            System.out.println("##### GOTAction run 55 " );
            classWriter.visitEnd();
            try {
                FileOutputStream os;
                File file = new File(fileName + ".class");
                file.createNewFile();
                os = new FileOutputStream(file);
                os.write(classWriter.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
