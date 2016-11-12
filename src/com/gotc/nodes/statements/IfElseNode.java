package com.gotc.nodes.statements;

import com.gotc.nodes.GOTNode;
import com.gotc.nodes.OneOperandNode;
import com.gotc.util.DeclarationDictionary;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 12-Nov-16.
 */
public class IfElseNode extends GOTNode {

    private final Label ifEnd = new Label();
    private final Label elseBegin = new Label();

    private IfNode ifNode;
    private ElseNode elseNode;

    private void buildIf(MethodVisitor mVisitor, DeclarationDictionary dict) {
        visitExpression(mVisitor, ifNode.getExpression(), dict);
        mVisitor.visitJumpInsn(IFEQ, elseNode == null ? ifEnd : elseBegin);
        dict.takeSnapshot();
        ifNode.iterate(mVisitor, dict);
        dict.restoreSnapshot();
        mVisitor.visitLabel(ifEnd);
    }

    private void buildElse(MethodVisitor mVisitor, DeclarationDictionary dict) {
        dict.takeSnapshot();
        mVisitor.visitJumpInsn(GOTO, ifEnd);
        mVisitor.visitLabel(elseBegin);
        elseNode.iterate(mVisitor, dict);
        dict.restoreSnapshot();
    }

    public GOTNode ifNode(StringVar expression) {
        System.out.println("##### IfElseNode ifNode 39 " );
        if (ifNode == null) {
            ifNode = new IfNode(expression);
        }
        return ifNode;
    }

    public GOTNode elseNode() {
        System.out.println("##### IfElseNode elseNode 47 " );
        if (elseNode == null) {
            elseNode = new ElseNode();
        }
        return elseNode;
    }

    private class IfNode extends OneOperandNode {

        IfNode(StringVar expression) {
            super(expression);
        }

        @Override
        public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
            buildIf(mVisitor, dict);
        }
    }

    private class ElseNode extends GOTNode {

        @Override
        public void build(MethodVisitor mVisitor, DeclarationDictionary dict) {
            buildElse(mVisitor, dict);
        }
    }
}
