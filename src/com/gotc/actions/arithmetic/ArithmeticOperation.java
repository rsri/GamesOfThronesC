package com.gotc.actions.arithmetic;

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
 * Created by srikaram on 09-Nov-16.
 */
abstract class ArithmeticOperation implements Action<Object>, Opcodes {

    boolean writeOperation(Context<Object> context, int opcode) {
        ValueStack stack = context.getValueStack();
        String expression = (String) stack.pop();
        String variable = (String) stack.pop();
        Pair pair = (Pair) stack.peek();
        MethodVisitor visitor = (MethodVisitor) pair.writer;
        DeclarationDictionary dictionary = pair.dictionary;
        int varPosition = dictionary.getVariableIndex(variable);
        if (varPosition != -1) {
            visitor.visitVarInsn(ILOAD, varPosition);
        } else {
            Util.constructError(context, "Variable not found : " + variable);
            return false;
        }
        if (Util.isNumber(expression)) {
            visitor.visitIntInsn(BIPUSH, Integer.parseInt(expression));
        } else {
            int position = dictionary.getVariableIndex(expression);
            if (position != -1) {
                visitor.visitVarInsn(ILOAD, position);
            } else {
                Util.constructError(context, "Variable not found : " + expression);
                return false;
            }
        }
        visitor.visitInsn(opcode);
        visitor.visitVarInsn(ISTORE, varPosition);
        return true;
    }

}
