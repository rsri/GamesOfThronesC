package com.gotc.nodes.method;

import com.gotc.nodes.GOTNode;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

/**
 * Created by srikaram on 11-Nov-16.
 */
abstract class AbstractMethodNode extends GOTNode {
    private final String methodName;
    private final String[] args;
    private final String thirdVar;
    private final int argsLength;

    AbstractMethodNode(StringVar methodName, StringVar[] args, StringVar thirdVar) {
        this.methodName = methodName.get();
        this.argsLength = args.length;
        this.args = new String[argsLength];
        convert(args);
        this.thirdVar = thirdVar.get();
    }

    private void convert(Var<String>[] args) {
        for (int i = 0 ; i < args.length ; i++) {
            this.args[i] = args[i].get();
        }
    }

    public String getMethodName() {
        return methodName;
    }

    String[] getArgs() {
        return args;
    }

    public int getArgsCount() {
        return argsLength;
    }

    String getThirdVar() {
        return thirdVar;
    }
}

