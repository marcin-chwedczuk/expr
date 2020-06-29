package pl.marcinchwedczuk.expr;

import java.util.Objects;

public class InputPosition {
    // 1 based line number.
    public final int lineNumber;

    // 1 based column number.
    public final int columnNumber;

    public InputPosition(int lineNumber, int columnNumber) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputPosition that = (InputPosition) o;
        return lineNumber == that.lineNumber &&
                columnNumber == that.columnNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, columnNumber);
    }

    @Override
    public String toString() {
        return "(" + lineNumber + ", " + columnNumber + ")";
    }
}
