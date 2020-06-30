package pl.marcinchwedczuk.expr;

import java.util.List;

public interface AstPostorderTransformer<R> {
    R constant(double c);
    R variable(String variableName);

    R binaryOperator(AstType type,
                     R leftOperand,
                     R rightOperand);

    R unaryOperator(AstType type,
                    R operand);

    R functionCall(String functionName,
                   List<R> arguments);
}
