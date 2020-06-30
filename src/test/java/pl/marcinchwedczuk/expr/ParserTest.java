package pl.marcinchwedczuk.expr;

import org.junit.Test;
import pl.marcinchwedczuk.expr.transformers.PrintToStringTransformer;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParserTest {
    @Test public void smoke_test() {
        List<Token> tokens =  Lexer.tokenize(Input.of("3*sin(x)+x^3/3"));
        Parser p = new Parser(tokens);
        Ast expr = p.gExpr();

        String exprString = PrintToStringTransformer.transform(expr);
        assertEquals("3*sin(x) + x^3/3", exprString);
    }

    @Test public void smoke_test2() {
        List<Token> tokens =  Lexer.tokenize(Input.of("-2^-3^-4"));
        Parser p = new Parser(tokens);
        Ast expr = p.gExpr();

        String exprString = PrintToStringTransformer.transform(expr);
        assertEquals("-2^-3^-4", exprString);
    }
}
