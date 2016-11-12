package com.gotc.nodes.method;

import com.gotc.nodes.GOTNode;
import com.gotc.util.DeclarationDictionary;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class MainMethodNode extends GOTNode implements IMethodNode {
    @Override
    public String getMethodName() {
        return "-1";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public boolean isNonVoid() {
        return false;
    }

    @Override
    public void build(ClassWriter cVisitor, DeclarationDictionary dict) {
        MethodVisitor visitor = cVisitor.visitMethod(ACC_PUBLIC + ACC_STATIC,
                "main", "([Ljava/lang/String;)V", null, null);
        dict.putVariable("-1"); // Replacement for String[] args
        dict.setCurrentMethod(dict.getMethod("-1", 1));
        iterate(visitor, dict);
        dict.clearVariables();
        visitor.visitInsn(RETURN);
        visitor.visitMaxs(100, 100);
        visitor.visitEnd();
    }
}
