package com.gotc.util;

import java.util.*;

/**
 * Created by srikaram on 08-Nov-16.
 */
public class DeclarationDictionary {

    private Map<String, Integer> variableMap = new HashMap<>();
    private final List<Method> methods = new ArrayList<>();

    private final Stack<Map<String, Integer>> snapshots = new Stack<>();

    private Method currentMethod;
    private String className;

    public boolean addMethod(String methodName, int argsCount, boolean nonVoid) {
        Method method = new Method(methodName, argsCount, nonVoid);
        return methods.add(method);
    }

    public Method getMethod(String methodName, int argsCount) {
        int index = methods.indexOf(new Method(methodName, argsCount));
        if (index == -1) {
            return null;
        }
        return methods.get(index);
    }

    public void setCurrentMethod(Method currentMethod) {
        this.currentMethod = currentMethod;
    }

    public Method getCurrentMethod() {
        return currentMethod;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int putVariable(String variableName) {
        if (variableMap.containsKey(variableName)) {
            return -1;
        }
        int value = variableMap.size();
        variableMap.put(variableName, value);
        return value;
    }

    public int getVariableIndex(String varName) {
        Integer value = variableMap.get(varName);
        return value == null ? -1 : value;
    }

    public void takeSnapshot() {
        Map<String, Integer> newVariableMap = new HashMap<>(variableMap);
        snapshots.push(variableMap);
        variableMap = newVariableMap;
    }

    public void restoreSnapshot() {
        variableMap = snapshots.pop();
    }

    public void clearVariables() {
        variableMap.clear();
    }

    public final class Method {
        private final String methodName;
        private final int argsCount;
        private final boolean nonVoid;

        Method(String methodName, int argsCount) {
            this.methodName = methodName;
            this.argsCount = argsCount;
            this.nonVoid = false;
        }

        Method(String methodName, int argsCount, boolean nonVoid) {
            this.methodName = methodName;
            this.argsCount = argsCount;
            this.nonVoid = nonVoid;
        }

        public String getMethodName() {
            return methodName;
        }

        public int getArgsCount() {
            return argsCount;
        }

        public boolean isNonVoid() {
            return nonVoid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Method that = (Method) o;

            if (argsCount != that.argsCount) {
                return false;
            }
            return methodName != null ? methodName.equals(that.methodName) : that.methodName == null;

        }

        @Override
        public int hashCode() {
            int result = methodName != null ? methodName.hashCode() : 0;
            result = 31 * result + argsCount;
            return result;
        }
    }
}
