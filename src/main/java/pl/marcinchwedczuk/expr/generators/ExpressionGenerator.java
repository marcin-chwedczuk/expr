package pl.marcinchwedczuk.expr.generators;

import com.google.common.util.concurrent.FutureCallback;
import pl.marcinchwedczuk.expr.AstType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ExpressionGenerator {
    private final ThreadLocalRandom rand = ThreadLocalRandom.current();
    private final List<Function> mathFunctions = Function.mathFunctions();

    private AstType chooseType() {
        var values = AstType.values();
        var index = rand.nextInt(0, values.length);
        return values[index];
    }

    private static class Function {
        public static Function of(String name, int arity) {
            return new Function(name, arity);
        }

        public static List<Function> mathFunctions() {
            return Arrays.stream(Math.class.getDeclaredMethods())
                    .filter(m -> {
                        return m.getReturnType().equals(double.class) &&
                            Arrays.stream(m.getParameterTypes())
                            .allMatch(p -> p.equals(double.class));
                    })
                    .map(m -> Function.of(m.getName(), m.getParameterCount()))
                    .collect(toList());
        }

        public final String name;
        public final int arity;

        public Function(String name, int arity) {
            this.name = name;
            this.arity = arity;
        }
    }
}
