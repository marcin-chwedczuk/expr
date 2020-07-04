package pl.marcinchwedczuk.expr.transformers.cglib;

import pl.marcinchwedczuk.expr.*;
import pl.marcinchwedczuk.expr.transformers.common.EvaluableExpression;
import pl.marcinchwedczuk.expr.transformers.common.Function;
import pl.marcinchwedczuk.expr.transformers.common.Functions;
import pl.marcinchwedczuk.expr.transformers.common.Variables;

import java.util.Map;

public class CGLibDemo {
    public static void main(String[] args) throws Exception {
        Ast ast = Pipeline.value("rand() + x")
                .map(Input::of)
                .map(Lexer::tokenize)
                .map(Parser::parse)
                .get();

        Class<EvaluableExpression> clazz = CompileTransformer.compile(ast);


        EvaluableExpression expr = clazz
                .getDeclaredConstructor()
                .newInstance();

        var variables = new Variables();
        variables.set("x", 1.0);
        variables.set("y", 10.0);

        var functions = new Functions();
        functions.register(Function.fromJava("sin", Math::sin));
        functions.register(Function.fromJava("cos", Math::cos));
        functions.register(Function.fromJava("rand", () -> 42));

        System.out.println("expr: " + expr.evaluate(variables, functions));
    }
}
