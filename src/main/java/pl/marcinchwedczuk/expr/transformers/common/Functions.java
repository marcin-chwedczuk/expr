package pl.marcinchwedczuk.expr.transformers.common;

import java.util.HashMap;
import java.util.Map;

public class Functions {
    private final Map<String, Function> functions = new HashMap<>();

    public Function get(String functionName) {
        var function = functions.get(functionName);
        if (function == null) {
            throw new RuntimeException("Function '" + functionName + "' is not defined.");
        }

        return function;
    }

    public void register(Function function) {
        functions.put(function.name(), function);
    }
}
