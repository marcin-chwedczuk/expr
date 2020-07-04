package pl.marcinchwedczuk.expr.transformers.interpreter;

import pl.marcinchwedczuk.expr.AstPostorderTransformer;
import pl.marcinchwedczuk.expr.AstType;
import pl.marcinchwedczuk.expr.transformers.common.EvaluableExpression;
import pl.marcinchwedczuk.expr.transformers.common.FunctionBody;

import java.util.List;
import java.util.function.DoubleSupplier;

public class InterpreterTransformer
        implements AstPostorderTransformer<EvaluableExpression> {

    @Override
    public EvaluableExpression constant(double c) {
        return null;
    }

    @Override
    public EvaluableExpression variable(String variableName) {
        return null;
    }

    @Override
    public EvaluableExpression binaryOperator(AstType type, EvaluableExpression leftOperand, EvaluableExpression rightOperand) {
        return null;
    }

    @Override
    public EvaluableExpression unaryOperator(AstType type, EvaluableExpression operand) {
        return null;
    }

    @Override
    public EvaluableExpression functionCall(String functionName, List<EvaluableExpression> arguments) {
        return null;
    }
}
