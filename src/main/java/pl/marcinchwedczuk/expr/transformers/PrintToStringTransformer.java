package pl.marcinchwedczuk.expr.transformers;

import pl.marcinchwedczuk.expr.AstTransformer;
import pl.marcinchwedczuk.expr.AstType;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class PrintToStringTransformer implements AstTransformer<String> {
    @Override
    public String constant(double c) {
        return Double.toString(c);
    }

    @Override
    public String variable(String variableName) {
        return variableName;
    }

    @Override
    public String binaryOperator(AstType type, String leftOperand, String rightOperand) {
        return "(" + leftOperand + " " + operatorString(type) +
                " " + rightOperand + ")";
    }

    @Override
    public String unaryOperator(AstType type, String operand) {
        return operatorString(type) + operand;
    }

    @Override
    public String functionCall(String functionName, List<String> arguments) {
        return functionName + "(" + String.join(", ", arguments) + ")";
    }

    private String operatorString(AstType t) {
        return switch (t) {
            case NEGATE, SUBTRACT -> "-";
            case ADD -> "+";
            case MULTIPLY -> "*";
            case DIVIDE -> "/";
            case MODULO -> "%";
            case EXPONENTIATE -> "^";
            default -> throw new IllegalArgumentException();
        };
    }
}
