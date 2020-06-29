package pl.marcinchwedczuk.expr;

public enum TokenType {
    LPAREN, RPAREN,

    PLUS, MINUS,
    MULTIPLICATION_SIGN, DIVISION_SIGN, MODULO_SIGN,

    EXPONENT,

    NUMBER, IDENTIFIER,

    COMMA,

    EOF, INVALID
}
