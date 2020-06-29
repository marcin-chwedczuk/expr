package pl.marcinchwedczuk.expr;

public enum AstType {
    NEGATE,
    ADD, SUBTRACT,
    MULTIPLY, DIVIDE, MODULO,
    EXPONENTIATE,

    CONSTANT, VARIABLE,

    FUNCTION_CALL
}
