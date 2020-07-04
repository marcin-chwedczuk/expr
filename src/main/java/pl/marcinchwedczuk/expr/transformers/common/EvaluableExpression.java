package pl.marcinchwedczuk.expr.transformers.common;

public interface EvaluableExpression {
    double evaluate(Variables variables, Functions functions);
}
