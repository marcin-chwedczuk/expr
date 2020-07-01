package pl.marcinchwedczuk.expr;

import org.junit.Test;
import pl.marcinchwedczuk.expr.transformers.PrintToStringTransformer;

import java.text.DecimalFormat;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParserTest {
    @Test public void basic_operators_delta() {
        Ast expr = parse("sqrt(b^2 - 4*a*c)");

        String expected = "sqrt(sub(pow(b, 2), mul(mul(4, a), c)))";
        String actual = asFunctionCalls(expr);
        assertEquals(expected, actual);
    }

    @Test public void basic_operators_x0() {
        Ast expr = parse("(-b - sqrt(delta))/2/a");

        String expected = "div(div(sub(neg(b), sqrt(delta)), 2), a)";
        String actual = asFunctionCalls(expr);
        assertEquals(expected, actual);
    }

    @Test public void mixing_exp_with_negative_signs() {
        // -2^-3^-4 == -(2^-(3^-4))
        Ast expr = parse("-2^-3^-4");

        String expected = "neg(pow(2, neg(pow(3, neg(4)))))";
        String actual = asFunctionCalls(expr);
        assertEquals(expected, actual);
    }

    @Test public void smoke_test() {
        Ast expr = parse("3*sin(x)+x^3/3");

        String exprString = PrintToStringTransformer.transform(expr);
        assertEquals("3*sin(x) + x^3/3", exprString);
    }

    @Test public void smoke_test2() {
        Ast expr = parse("-2^-3^-4");

        String exprString = PrintToStringTransformer.transform(expr);
        assertEquals("-2^-3^-4", exprString);
    }

    private static Ast parse(String input) {
        return Pipeline.value(input)
                .map(Input::of)
                .map(Lexer::tokenize)
                .map(Parser::parse)
                .get();
    }

    private static void assertEqualsIgnoreWhitespaces(
            String expected, String actual)
    {
        expected = expected.replaceAll("\\s+", "");
        actual = actual.replaceAll("\\s+", "");

        assertEquals(expected, actual);
    }

    private static String asFunctionCalls(Ast ast) {
        return ast.postOrder(new StructurePrintingTransformer());
    }

    private static class StructurePrintingTransformer
            implements AstPostorderTransformer<String> {

        private final DecimalFormat wholeNumbersFormat = new DecimalFormat("#");

        @Override
        public String constant(double c) {
            return wholeNumbersFormat.format(c);
        }

        @Override
        public String variable(String variableName) {
            return variableName;
        }

        @Override
        public String binaryOperator(AstType type, String leftOperand, String rightOperand) {
            return String.format("%s(%s, %s)", op(type), leftOperand, rightOperand);
        }

        @Override
        public String unaryOperator(AstType type, String operand) {
            return String.format("%s(%s)", op(type), operand);
        }

        @Override
        public String functionCall(String functionName, List<String> arguments) {
            String argumentsString = String.join(", ", arguments);
            return String.format("%s(%s)", functionName, argumentsString);
        }

        private String op(AstType type) {
            return switch (type) {
                case NEGATE -> "neg";
                case ADD -> "add";
                case SUBTRACT -> "sub";
                case MULTIPLY -> "mul";
                case DIVIDE -> "div";
                case MODULO -> "mod";
                case EXPONENTIATE -> "pow";
                default -> null;
            };
        }
    }
}
