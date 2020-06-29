package pl.marcinchwedczuk.expr;

public class Input {
    public static Input of(String s) {
        return new Input(s);
    }

    private static final int FIRST_COLUMN_NO = 1;
    private static final int FIRST_LINE_NO = 1;

    private final String input;
    private int curr = 0;

    private int lineNo = FIRST_LINE_NO;
    private int columnNo = FIRST_COLUMN_NO;

    private Input(String s) {
        this.input = s;
    }

    public InputPosition position() {
        return new InputPosition(lineNo, columnNo);
    }

    boolean eof() {
        return (curr >= input.length());
    }

    char current() {
        return eof()
                ? '\0'
                : input.charAt(curr);
    }

    void next() {
        if (!eof()) {
            char previousChar = current();
            curr++;

            if (previousChar == '\n') {
                lineNo++;
                columnNo = FIRST_COLUMN_NO;
            }
            else {
                columnNo++;
            }
        }
    }
}
