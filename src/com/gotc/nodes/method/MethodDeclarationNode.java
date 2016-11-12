package com.gotc.nodes.method;

import com.gotc.nodes.GOTNode;
import com.gotc.util.DeclarationDictionary;
import com.gotc.util.DeclarationDictionary.Method;
import com.gotc.util.Util;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;

import static com.gotc.util.Util.buildMethodSignature;

/**
 * Created by srikaram on 11-Nov-16.
 */
public class MethodDeclarationNode extends AbstractMethodNode implements IMethodNode {

    public MethodDeclarationNode(StringVar methodName, StringVar[] args, StringVar isNonVoid) {
        super(methodName, args, isNonVoid);
    }

    public boolean isNonVoid() {
        return Boolean.valueOf(getThirdVar());
    }

    @Override
    public void build(ClassWriter cVisitor, DeclarationDictionary dict) {
        Method method = dict.getMethod(getMethodName(), getArgsCount());
        if (method == null) {
            Util.constructError("Method %s not found", getMethodName());
        }
        dict.setCurrentMethod(method);
        MethodVisitor methodVisitor = cVisitor.visitMethod(
                ACC_PRIVATE + ACC_STATIC,
                getMethodName(),
                buildMethodSignature(getArgsCount(), isNonVoid()),
                null,
                null);
        for (String arg : getArgs()) {
            if (dict.putVariable(arg) == -1) {
                Util.constructError("Duplicate variable declaration : " + arg);
            }
        }
        iterate(methodVisitor, dict);
        if (isNonVoid()) {
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitInsn(IRETURN);
        } else {
            methodVisitor.visitInsn(RETURN);
        }
        dict.clearVariables();
        methodVisitor.visitMaxs(100, 100);
        methodVisitor.visitEnd();
    }
}
