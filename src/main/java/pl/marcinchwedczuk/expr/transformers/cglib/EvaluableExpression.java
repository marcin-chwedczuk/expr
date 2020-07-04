package pl.marcinchwedczuk.expr.transformers.cglib;

import java.util.Map;

public interface EvaluableExpression {
    double evaluate(Map<String, Double> variables,
                    Map<String, Function> functions);
}
