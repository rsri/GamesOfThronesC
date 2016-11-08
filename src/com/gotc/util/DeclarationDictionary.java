package com.gotc.util;

import java.util.*;

/**
 * Created by srikaram on 08-Nov-16.
 */
public class DeclarationDictionary {

    private Map<String, Integer> variableMap = new HashMap<>();
    private final Set<MethodDescription> methodDescriptions = new HashSet<>();

    public boolean addMethodDescription(String methodName, int argsCount, boolean nonVoid) {
        MethodDescription description = new MethodDescription(methodName, argsCount, nonVoid);
        return methodDescriptions.add(description);
    }

    public int putVariable(String variableName) {
        if (variableMap.containsKey(variableName)) {
            return -1;
        }
        int value = variableMap.size() + 1;
        variableMap.put(variableName, value);
        return value;
    }

    public int getVariableIndex(String varName) {
        Integer value = variableMap.get(varName);
        return value == null ? -1 : value;
    }

    public void clearVariables() {
        variableMap.clear();
    }

    public final class MethodDescription {
        private final String methodName;
        private final int argsCount;
        private final boolean nonVoid;

        MethodDescription(String methodName, int argsCount, boolean nonVoid) {
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

            MethodDescription that = (MethodDescription) o;

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
