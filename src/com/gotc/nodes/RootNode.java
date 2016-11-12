package com.gotc.nodes;

import com.gotc.nodes.method.IMethodNode;
import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Util;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class RootNode extends OneOperandNode {

    public RootNode(StringVar className) {
        super(className);
    }

    @Override
    public void build() {
        DeclarationDictionary dictionary = new DeclarationDictionary();
        dictionary.setClassName(getExpression());
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(V1_6, ACC_PUBLIC, getExpression(), null,
                getInternalName(Object.class), null);
        MethodVisitor constructor = classWriter.visitMethod(ACC_PUBLIC,
                "<init>", "()V", null, null);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, getInternalName(Object.class), "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();
        for (GOTNode child : getChildren()) {
            if (!(child instanceof IMethodNode)) {
                Util.constructError("Incorrect hierarchy.");
            }
            IMethodNode node = ((IMethodNode) child);
            dictionary.addMethod(node.getMethodName(), node.getArgsCount(), node.isNonVoid());
        }
        getChildren().forEach(child -> child.build(classWriter, dictionary));
        classWriter.visitEnd();
        try {
            FileOutputStream os;
            File file = new File(getExpression() + ".class");
            file.createNewFile();
            os = new FileOutputStream(file);
            os.write(classWriter.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
