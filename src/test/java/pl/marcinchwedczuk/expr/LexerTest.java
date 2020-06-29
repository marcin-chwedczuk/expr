package pl.marcinchwedczuk.expr;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class LexerTest {
    @Test public void number_token() {
        Token t = singleToken("0");
        assertNumber(t, "0");

        t = singleToken("123");
        assertNumber(t, "123");

        t = singleToken("1.");
        assertNumber(t, "1.");

        t = singleToken(".1");
        assertNumber(t, ".1");

        t = singleToken("1.32");
        assertNumber(t, "1.32");

        t = singleToken("12e10");
        assertNumber(t, "12e10");

        t = singleToken("12e+10");
        assertNumber(t, "12e+10");

        t = singleToken("12e-10");
        assertNumber(t, "12e-10");

        t = singleToken("1.2032e7");
        assertNumber(t, "1.2032e7");
    }

    @Test public void invalid_number_token() {
        assertError(".", "Invalid number literal '.'");
        assertError(".e7", "Invalid number literal '.e7'");
        assertError("1.22e", "Expected at least a single digit after E in '1.22e'");
    }

    @Test public void identifier_token() {
        Token t = singleToken("_abc");
        assertIdentifier(t, "_abc");

        t = singleToken("abc$123");
        assertIdentifier(t, "abc$123");

        t = singleToken("x");
        assertIdentifier(t, "x");
    }

    @Test public void handles_whitespaces() {
        List<Token> actual = tokenize("1 1\n2\n 3");
        List<Token> expected = Arrays.asList(
            new Token(TokenType.NUMBER, "1", new InputPosition(1,1)),
            new Token(TokenType.NUMBER, "1", new InputPosition(1,3)),
            new Token(TokenType.NUMBER, "2", new InputPosition(2,1)),
            new Token(TokenType.NUMBER, "3", new InputPosition(3,2)),
            new Token(TokenType.EOF, "", new InputPosition(3,3))
        );

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test public void smoke_test() {
        List<Token> actual = tokenize("2*3.1415*r^2");
        List<Token> expected = Arrays.asList(
            new Token(TokenType.NUMBER, "2", new InputPosition(1,1)),
            new Token(TokenType.MULTIPLICATION_SIGN, "*", new InputPosition(1,2)),
            new Token(TokenType.NUMBER, "3.1415", new InputPosition(1,3)),
            new Token(TokenType.MULTIPLICATION_SIGN, "*", new InputPosition(1,9)),
            new Token(TokenType.IDENTIFIER, "r", new InputPosition(1,10)),
            new Token(TokenType.EXPONENT, "^", new InputPosition(1,11)),
            new Token(TokenType.NUMBER, "2", new InputPosition(1,12)),
            new Token(TokenType.EOF, "", new InputPosition(1,13))
        );

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    private static void assertError(String s, String expectedProblem) {
        try {
            tokenize(s);
            fail("No error was thrown for " + s);
        }
        catch (ParseErrorException e) {
            assertEquals(expectedProblem, e.problem);
        }
    }

    private static void assertNumber(Token t, String expectedText) {
        assertEquals("Token type", TokenType.NUMBER, t.type);
        assertEquals("Token text", expectedText, t.text);
    }

    private static void assertIdentifier(Token t, String expectedText) {
        assertEquals("Token type", TokenType.IDENTIFIER, t.type);
        assertEquals("Token text", expectedText, t.text);
    }

    private static void assertPosition(Token t, int lineNo, int columnNo) {
        assertEquals("Line number", lineNo, t.position.lineNumber);
        assertEquals("Column number", columnNo, t.position.columnNumber);
    }

    private static List<Token> tokenize(String s) {
        return Lexer.tokenize(Input.of(s));
    }

    private static Token singleToken(String s) {
        List<Token> tokens = tokenize(s);

        // The second token will be EOF, so e.g. <2>, <EOF>
        assertEquals(
            "Expected a single token but list contained " + tokens.size() + ".",
            2, tokens.size());

        return tokens.get(0);
    }
}
