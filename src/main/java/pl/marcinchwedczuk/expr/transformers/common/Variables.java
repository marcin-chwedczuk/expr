package pl.marcinchwedczuk.expr.transformers.common;

import java.util.HashMap;
import java.util.Map;

public class Variables {
    private final Map<String, Double> variables = new HashMap<>();

    public double get(String variableName) {
        var variableValue = variables.get(variableName);
        if (variableValue == null) {
            throw new RuntimeException("Variable '" + variableName + "' is not defined.");
        }

        return variableValue;
    }

    public void set(String variableName, double value) {
        variables.put(variableName, value);
    }
}
