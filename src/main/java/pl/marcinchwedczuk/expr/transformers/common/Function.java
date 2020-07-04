package pl.marcinchwedczuk.expr.transformers.common;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

public class Function {
    public static Function fromJava(String name, DoubleSupplier f0) {
        return new Function(name, 0, args -> f0.getAsDouble());
    }

    public static Function fromJava(String name, DoubleUnaryOperator f1) {
        return new Function(name, 1, args -> f1.applyAsDouble(args[0]));
    }

    public static Function fromJava(String name, DoubleBinaryOperator f2) {
        return new Function(name, 2, args -> f2.applyAsDouble(args[0], args[1]));
    }

    private final String name;
    private final int arity;
    private final FunctionBody body;

    private Function(String name, int arity, FunctionBody body) {
        this.name = name;
        this.arity = arity;
        this.body = body;
    }

    public String name() { return name; }

    public double call(double[] arguments) {
        if (arguments == null) {
            throw new NullPointerException("arguments");
        }

        if (arguments.length != arity) {
            throw new IllegalArgumentException(
                    "Invalid number of arguments passed to function " +
                            name + ". Function expected " + arity +
                            " arguments.");
        }

        return body.execute(arguments);
    }
}

