package pl.marcinchwedczuk.expr.transformers.common;

@FunctionalInterface
public interface FunctionBody {
    double execute(double[] arguments);
}
