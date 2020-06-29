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

        AstTransformer<String> toString = new PrintToStringTransformer();
        String exprString = expr.postOrder(toString);
        assertEquals(exprString, "");
    }
}
