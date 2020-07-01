package pl.marcinchwedczuk.expr;

import java.util.function.Function;

public class Pipeline<V> {
    public static <V> Pipeline<V> value(V value) {
        return new Pipeline<>(value);
    }

    private final V value;

    private Pipeline(V value) {
        this.value = value;
    }

    public <R> Pipeline<R> map(Function<V, R> mapper) {
        return Pipeline.value(mapper.apply(value));
    }

    public V get() { return value; }
}
