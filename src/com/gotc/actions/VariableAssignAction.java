package com.gotc.actions;

import com.gotc.util.DeclarationDictionary;
import com.gotc.util.Pair;
import com.gotc.util.Util;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.errors.BasicParseError;
import org.parboiled.support.ValueStack;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class VariableAssignAction implements Action<Object>, Opcodes {
    @Override
    public boolean run(Context<Object> context) {
        ValueStack stack = context.getValueStack();
        String varName = (String) stack.pop(1);
        String value = (String) stack.pop();
        Pair pair = (Pair) stack.peek(stack.size() - 1);
        MethodVisitor visitor = (MethodVisitor) pair.writer;
        DeclarationDictionary dictionary = pair.dictionary;
        if (dictionary.getVariableIndex(varName) != -1) {
            Util.constructError(context, "Duplicate variable declaration : " + varName);
        }
        int position = dictionary.putVariable(varName);
        int assignedVarPos = dictionary.getVariableIndex(value);
        if (!Util.isNumber(value) && assignedVarPos == -1) {
            Util.constructError(context, "Variable not found : " + value);
            return false;
        }
        if (position == -1) {
            Util.constructError(context, "Variable not found : " + varName);
            return false;
        }
        if (assignedVarPos != -1) {
            visitor.visitVarInsn(ILOAD, assignedVarPos);
        } else {
            int intVal = Integer.parseInt(value);
            if (intVal == 0) {
                visitor.visitInsn(ICONST_0);
            } else if (intVal == 1) {
                visitor.visitInsn(ICONST_1);
            } else {
                visitor.visitIntInsn(BIPUSH, intVal);
            }
        }
        visitor.visitVarInsn(ISTORE, position);
        return true;
    }
}
