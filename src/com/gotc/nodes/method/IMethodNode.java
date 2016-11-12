package com.gotc.nodes.method;

/**
 * Created by srikaram on 11-Nov-16.
 */
public interface IMethodNode {

    String getMethodName();

    int getArgsCount();

    boolean isNonVoid();
}
