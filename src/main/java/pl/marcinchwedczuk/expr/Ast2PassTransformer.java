package pl.marcinchwedczuk.expr;

import java.util.List;

public interface Ast2PassTransformer<K,R> {
    K binaryOperatorContext(AstType type, K parentContext);
    K unaryOperatorContext(AstType type, K parentContext);
    K functionCallContext(String functionName, K parentContext);

    R constant(double c, K context);
    R variable(String variableName, K context);

    R binaryOperator(AstType type,
                     R leftOperand,
                     R rightOperand,
                     K context);

    R unaryOperator(AstType type,
                    R operand,
                    K context);

    R functionCall(String functionName,
                   List<R> arguments,
                   K context);
}
