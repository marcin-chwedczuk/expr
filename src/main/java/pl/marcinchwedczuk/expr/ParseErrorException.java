package pl.marcinchwedczuk.expr;

public class ParseErrorException extends RuntimeException {
    public final InputPosition position;
    public final String problem;

    public ParseErrorException(InputPosition position,
                               String problem) {
        super("error: " + position.toString() + ": " + problem);

        this.position = position;
        this.problem = problem;
    }
}
