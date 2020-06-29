package pl.marcinchwedczuk.expr;

import java.util.Objects;

public class Token {
    public final TokenType type;
    public final String text;
    public final InputPosition position;

    public Token(TokenType type, String text, InputPosition position) {
        this.type = type;
        this.text = text;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type &&
                Objects.equals(text, token.text) &&
                Objects.equals(position, token.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, text, position);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", type, text);
    }
}
