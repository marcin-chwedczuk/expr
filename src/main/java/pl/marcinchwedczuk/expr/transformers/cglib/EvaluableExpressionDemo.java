package pl.marcinchwedczuk.expr.transformers.cglib;

import java.util.Map;

public class EvaluableExpressionDemo implements EvaluableExpression {
    @Override
    public double evaluate(Map<String, Double> variables, Map<String, Function> functions) {
        double var = variables.get("x");
        Function func = functions.get("sin");
        fjut(3,2);
        return func.call(new double[] { var });
    }

    public void callEvaluate() {
        evaluate(Map.of(), Map.of());
    }

    public static double fjut(double a, double b) { return a + b; }
}
