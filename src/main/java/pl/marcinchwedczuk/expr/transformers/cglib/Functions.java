package pl.marcinchwedczuk.expr.transformers.cglib;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

public class Functions {
    public static Function fromJava(DoubleSupplier f0) {
        return new Function() {
            @Override
            public double call(double[] arguments) {
                if (arguments == null || arguments.length != 0)
                    throw new IllegalArgumentException("arguments");

                return f0.getAsDouble();
            }
        };
    }

    public static Function fromJava(DoubleUnaryOperator f1) {
        return new Function() {
            @Override
            public double call(double[] arguments) {
                if (arguments.length != 1)
                    throw new IllegalArgumentException("arguments");

                return f1.applyAsDouble(arguments[0]);
            }
        };
    }

    public static Function fromJava(DoubleBinaryOperator f2) {
        return new Function() {
            @Override
            public double call(double[] arguments) {
                if (arguments.length != 2)
                    throw new IllegalArgumentException("arguments");

                return f2.applyAsDouble(arguments[0], arguments[1]);
            }
        };
    }
}
