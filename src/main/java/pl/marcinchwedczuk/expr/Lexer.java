package pl.marcinchwedczuk.expr;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    public static List<Token> tokenize(Input input) {
        return new Lexer(input).tokenize();
    }

    private final Input input;
    private final List<Token> tokens = new ArrayList<>();

    private Lexer(Input input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        while (!input.eof()) {
            extractToken();
        }
        tokens.add(new Token(TokenType.EOF, "", input.position()));

        return tokens;
    }

    private void extractToken() {
        char c = input.current();

        if (Character.isWhitespace(c)) {
            input.next();
            return;
        }

        // Number 1.00 or .123
        if (Character.isDigit(c) || (c == '.')) {
            number();
            return;
        }

        if (Character.isJavaIdentifierStart(c)) {
            identifier();
            return;
        }

        switch (c) {
            case '+': singleCharToken(TokenType.PLUS); return;
            case '-': singleCharToken(TokenType.MINUS); return;
            case '*': singleCharToken(TokenType.MULTIPLICATION_SIGN); return;
            case '/': singleCharToken(TokenType.DIVISION_SIGN); return;
            case '%': singleCharToken(TokenType.MODULO_SIGN); return;
            case '^': singleCharToken(TokenType.EXPONENT); return;
            case '(': singleCharToken(TokenType.LPAREN); return;
            case ')': singleCharToken(TokenType.RPAREN); return;
            case ',': singleCharToken(TokenType.COMMA); return;
        }

        throw new ParseErrorException(input.position(),
            "Unexpected input character: '" + input.current() + "'.");
    }

    private void singleCharToken(TokenType type) {
        tokens.add(new Token(type,
                Character.toString(input.current()),
                input.position()));

        input.next();
    }

    private void number() {
        // Invalid .
        // Number 3.123e+7 or .032e7 or 1. or .123
        StringBuilder text = new StringBuilder();
        InputPosition start = input.position();

        boolean missingDigits = true;
        boolean missingExponentDigits = false;

        // Digits before .
        while (Character.isDigit(input.current())) {
            text.append(input.current());
            missingDigits = false;
            input.next();
        }

        if (input.current() == '.') {
            text.append('.');
            input.next();

            // Digits after .
            while (Character.isDigit(input.current())) {
                text.append(input.current());
                missingDigits = false;
                input.next();
            }
        }

        // The e or E
        if (Character.toLowerCase(input.current()) == 'e') {
            text.append(input.current());
            input.next();
            missingExponentDigits = true;

            // Optional sign after E
            if (input.current() == '+' || input.current() == '-') {
                text.append(input.current());
                input.next();
            }

            // Digits after E
            while (Character.isDigit(input.current())) {
                text.append(input.current());
                missingExponentDigits = false;
                input.next();
            }
        }

        if (missingDigits) {
            throw new ParseErrorException(
                    start, "Invalid number literal '" + text.toString() + "'");
        }

        if (missingExponentDigits) {
            throw new ParseErrorException(
                    input.position(),
                    "Expected at least a single digit after E in '" + text.toString() + "'");
        }

        tokens.add(new Token(TokenType.NUMBER, text.toString(), start));
    }

    private void identifier() {
        StringBuilder text = new StringBuilder();
        InputPosition start = input.position();

        do {
            text.append(input.current());
            input.next();
        } while (!input.eof() &&
                 Character.isJavaIdentifierPart(input.current()));

        tokens.add(new Token(TokenType.IDENTIFIER, text.toString(), start));
    }
}
