package pl.marcinchwedczuk.expr;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Ast {
    public static Ast newConstant(double c) {
        return new Ast(AstType.CONSTANT,
                null, null,
                null, null,
                c, null);
    }

    public static Ast newVariable(String variableName) {
        return new Ast(AstType.VARIABLE,
                null, null,
                null, null,
                Double.NaN, variableName);
    }

    public static Ast newFunctionCall(String functionName, List<Ast> arguments) {
        return new Ast(AstType.FUNCTION_CALL,
                null, null,
                functionName, arguments,
                Double.NaN, null);
    }

    public static Ast newBinaryOperator(AstType t, Ast left, Ast right) {
        switch (t) {
            case ADD: case SUBTRACT:
            case MULTIPLY: case DIVIDE: case MODULO:
            case EXPONENTIATE:
                break;

            default:
                throw new IllegalArgumentException("Not a binary operator: " + t);
        }

        return new Ast(t,
                left, right,
                null, null,
                Double.NaN, null);
    }

    public static Ast newUnaryOperator(AstType t, Ast operand) {
        if (t != AstType.NEGATE)
            throw new IllegalArgumentException("Not an unary operator: " + t);

        return new Ast(t,
                operand, null,
                null, null,
                Double.NaN, null);
    }


    public final AstType type;

    // Operator
    public final Ast left;
    public final Ast right;

    // Function call
    public final String functionName;
    public final List<Ast> arguments;

    // Constant or variable
    public final double constant;
    public final String variableName;

    private Ast(AstType type,
               Ast left, Ast right,
               String functionName,
               List<Ast> arguments,
               double constant,
               String variableName) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.functionName = functionName;
        this.arguments = arguments;
        this.constant = constant;
        this.variableName = variableName;
    }

    public <R> R postOrder(AstPostorderTransformer<R> transformer) {
        return switch (type) {
            case NEGATE ->
                    transformer.unaryOperator(type, left.postOrder(transformer));

            case ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, EXPONENTIATE ->
                    transformer.binaryOperator(type,
                            left.postOrder(transformer),
                            right.postOrder(transformer));

            case VARIABLE -> transformer.variable(variableName);
            case CONSTANT -> transformer.constant(constant);
            case FUNCTION_CALL -> transformer.functionCall(functionName,
                    arguments.stream()
                        .map(arg -> arg.postOrder(transformer))
                        .collect(toList()));
        };
    }

    public <K,R> R twoPass(Ast2PassTransformer<K,R> transformer,
                           K parentContext)
    {
        return switch (type) {
            case NEGATE -> {
                K context = transformer.unaryOperatorContext(type, parentContext);
                R childResult = left.twoPass(transformer, context);

                yield transformer.unaryOperator(type, childResult, parentContext);
            }

            case ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, EXPONENTIATE -> {
                K context = transformer.binaryOperatorContext(type, parentContext);
                R leftResult = left.twoPass(transformer, context);
                R rightResult = right.twoPass(transformer, context);

                yield transformer.binaryOperator(type,
                        leftResult, rightResult,
                        parentContext);
            }

            case VARIABLE ->
                transformer.variable(variableName, parentContext);

            case CONSTANT ->
                transformer.constant(constant, parentContext);

            case FUNCTION_CALL -> {
                K context = transformer.functionCallContext(functionName, parentContext);
                var argumentsResults = arguments.stream()
                        .map(arg -> arg.twoPass(transformer, context))
                        .collect(toList());

                yield transformer.functionCall(functionName, argumentsResults, parentContext);
            }
        };
    }
}
