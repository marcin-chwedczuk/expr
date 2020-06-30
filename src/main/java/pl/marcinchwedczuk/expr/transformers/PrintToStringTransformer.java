package pl.marcinchwedczuk.expr.transformers;

import pl.marcinchwedczuk.expr.Ast;
import pl.marcinchwedczuk.expr.AstPostorderTransformer;
import pl.marcinchwedczuk.expr.AstType;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
import static pl.marcinchwedczuk.expr.AstType.*;

public class PrintToStringTransformer
        implements AstPostorderTransformer<PrintToStringTransformer.SubtreeString> {

    public static String transform(Ast ast) {
        var result = ast.postOrder(new PrintToStringTransformer());
        return result.subtreeString;
    }

    @Override
    public SubtreeString constant(double c) {
        return SubtreeString.ofPrioritySubtreeString(
            operatorPriority(CONSTANT),
            toString(c));
    }

    private static String toString(double d) {
        return Double.toString(d)
            // remove lasts zeros
            .replaceFirst("0+$", "")
            // remove '.' if it is the last character
            .replaceFirst("\\.$", "");
    }

    @Override
    public SubtreeString variable(String variableName) {
        return SubtreeString.ofPrioritySubtreeString(
                operatorPriority(VARIABLE),
                variableName);
    }

    @Override
    public SubtreeString binaryOperator(AstType type,
                                        SubtreeString leftOperand,
                                        SubtreeString rightOperand) {
        int priority = operatorPriority(type);
        boolean addSpaces = (type == ADD || type == SUBTRACT);

        String leftSubstring = (leftOperand.operatorPriority < priority)
                ? addParentheses(leftOperand.subtreeString)
                : leftOperand.subtreeString;

        if (addSpaces) {
            leftSubstring = leftSubstring + " ";
        }

        // Edge case: we write 2^(3+1) but we also write 2^-3.
        // Edge case: -2^-3^-4
        boolean rightNeedsParentheses =
                (rightOperand.operatorPriority < priority) &&
                rightOperand.operator.map(op -> op == NEGATE).orElse(false);

        String rightSubstring = rightNeedsParentheses
                ? addParentheses(rightOperand.subtreeString)
                : rightOperand.subtreeString;

        if (addSpaces) {
            rightSubstring = " " + rightSubstring;
        }

        return SubtreeString.ofPrioritySubtreeString(
                priority,
                leftSubstring + operatorString(type) + rightSubstring);
    }

    @Override
    public SubtreeString unaryOperator(AstType type, SubtreeString operand) {
        return SubtreeString.ofPrioritySubtreeString(
                operatorPriority(type),
                operatorString(type) + operand.subtreeString);
    }

    @Override
    public SubtreeString functionCall(String functionName, List<SubtreeString> arguments) {
        String argumentsString = arguments.stream()
                .map(s -> s.subtreeString)
                .collect(joining(", "));

        return SubtreeString.ofPrioritySubtreeString(
                operatorPriority(FUNCTION_CALL),
                functionName + "(" + argumentsString + ")");
    }

    private static String addParentheses(String s) {
        return "(" + s + ")";
    }

    private static String operatorString(AstType t) {
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

    private static int operatorPriority(AstType type) {
        // Higher number == higher priority
        // Edge case: -2^5 == -(2^5)
        return switch (type) {
            case ADD, SUBTRACT -> 20;
            case MULTIPLY, DIVIDE, MODULO -> 40;
            case NEGATE -> 60;
            case EXPONENTIATE -> 80;
            case CONSTANT, VARIABLE, FUNCTION_CALL -> 100;
        };
    }

    public static class SubtreeString {
        public static SubtreeString ofPrioritySubtreeString(
            int operatorPriority, String subtreeString)
        {
            return new SubtreeString(operatorPriority, subtreeString, null);
        }

        public static SubtreeString ofPrioritySubtreeString(
                int operatorPriority, String subtreeString, AstType operator)
        {
            return new SubtreeString(operatorPriority, subtreeString, operator);
        }

        public final int operatorPriority;
        public final String subtreeString;

        public final Optional<AstType> operator;

        private SubtreeString(int operatorPriority,
                              String subtreeString,
                              AstType operator) {
            this.operatorPriority = operatorPriority;
            this.subtreeString = subtreeString;
            this.operator = Optional.ofNullable(operator);
        }
    }
}
