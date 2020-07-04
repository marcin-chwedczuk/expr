package pl.marcinchwedczuk.expr.transformers.cglib;

import pl.marcinchwedczuk.expr.*;

import java.util.Map;

public class CGLibDemo {
    public static void main(String[] args) throws Exception {
        Ast ast = Pipeline.value("2^3^4")
                .map(Input::of)
                .map(Lexer::tokenize)
                .map(Parser::parse)
                .get();

        Class<EvaluableExpression> clazz = CompileTransformer.compile(ast);


        EvaluableExpression expr = clazz
                .getDeclaredConstructor()
                .newInstance();

        var variables = Map.of(
                "x", 1.0,
                "y", 10.0);

        var functions = Map.of(
                "sin", Functions.fromJava(Math::sin),
                "cos", Functions.fromJava(Math::cos),
                "rand", Functions.fromJava(() -> 42.0));

        System.out.println("expr: " + expr.evaluate(variables, functions));
    }
}
