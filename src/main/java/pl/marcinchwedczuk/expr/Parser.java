package pl.marcinchwedczuk.expr;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    // Index of next not yet seen token
    private int curr = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token lookahead(int n) {
        int tmp = curr + n;
        if (tmp > tokens.size()) {
            tmp = tokens.size() - 1; // Return last token, EOF
        }
        return tokens.get(tmp);
    }

    private Token lookahead0() { return lookahead(0); }
    private Token lookahead1() { return lookahead(1); }

    private void consumeToken() {
        if ((curr+1) < tokens.size()) {
            curr++;
        }
    }

    private void consumeToken(TokenType t) {
        Token current = lookahead0();
        if (current.type != t) {
            throw new ParseErrorException(
                    current.position,
                    // TODO translate token type to descriptive name
                    "Expected " + t + " but found " + current.type);
        }

        consumeToken();
    }

    public Ast gExpr() {
        return gTerm();
    }

    private Ast gTerm() {
        Ast left = gFactor();

        while (isOneOf(lookahead0(), TokenType.PLUS, TokenType.MINUS)) {
            TokenType opType = lookahead0().type;
            consumeToken();

            Ast right = gFactor();
            left = Ast.newBinaryOperator(
                toBinaryOperatorType(opType), left, right);
        }

        return left;
    }

    private Ast gFactor() {
        Ast left = gPower();

        while (isOneOf(lookahead0(),
                TokenType.MULTIPLICATION_SIGN,
                TokenType.DIVISION_SIGN,
                TokenType.MODULO_SIGN))
        {
            TokenType opType = lookahead0().type;
            consumeToken();

            Ast right = gPower();
            left = Ast.newBinaryOperator(
                    toBinaryOperatorType(opType), left, right);
        }

        return left;
    }

    private Ast gPower() {
        List<Ast> values = new ArrayList<>();
        values.add(gValue());

        while (isOneOf(lookahead0(), TokenType.EXPONENT)) {
            consumeToken();
            values.add(gValue());
        }

        // Exp is right associative, so 1^2^3 is 1^(2^3)
        Ast tmp = values.get(values.size()-1);
        for (int i = values.size() - 2; i >= 0; i--) {
            tmp = Ast.newBinaryOperator(
                    AstType.EXPONENTIATE,
                    values.get(i), tmp);
        }

        return tmp;
    }

    private Ast gValue() {
        if (isOneOf(lookahead0(), TokenType.LPAREN)) {
            consumeToken();
            Ast expr = gExpr();
            consumeToken(TokenType.RPAREN);
            return expr;
        }

        if (isOneOf(lookahead0(), TokenType.NUMBER)) {
            Token number = lookahead0();
            consumeToken();
            try {
                double c = Double.parseDouble(number.text);
                return Ast.newConstant(c);
            }
            catch (NumberFormatException e) {
                throw new ParseErrorException(number.position,
                    "Invalid number literal '" + number.text + "': " + e.getMessage());
            }
        }

        if (isOneOf(lookahead0(), TokenType.IDENTIFIER)) {
            Token functionOrVariableName = lookahead0();
            consumeToken();

            if (isOneOf(lookahead0(), TokenType.LPAREN)) {
                consumeToken();
                List<Ast> args = gArguments();
                consumeToken(TokenType.RPAREN);

                return Ast.newFunctionCall(functionOrVariableName.text, args);
            }
            else {
                return Ast.newVariable(functionOrVariableName.text);
            }
        }

        boolean negate = false;
        while (isOneOf(lookahead0(), TokenType.PLUS, TokenType.MINUS)) {
            if (lookahead0().type == TokenType.MINUS) {
                negate = !negate;
            }
            consumeToken();
        }
        Ast tmp = gValue();
        if (negate) {
            tmp = Ast.newUnaryOperator(AstType.NEGATE, tmp);
        }
        return tmp;
    }

    private List<Ast> gArguments() {
        List<Ast> args = new ArrayList<>();

        if (isOneOf(lookahead0(), TokenType.RPAREN)) {
            return args;
        }

        args.add(gExpr());
        while (isOneOf(lookahead0(), TokenType.COMMA)) {
            consumeToken();
            args.add(gExpr());
        }

        return args;
    }

    private static AstType toBinaryOperatorType(TokenType t) {
        switch (t) {
            case PLUS: return AstType.ADD;
            case MINUS: return AstType.SUBTRACT;
            case MULTIPLICATION_SIGN: return AstType.MULTIPLY;
            case DIVISION_SIGN: return AstType.DIVIDE;
            case MODULO_SIGN: return AstType.MODULO;
            case EXPONENT: return AstType.EXPONENTIATE;

            default: throw new IllegalArgumentException("Not a binary operator: " + t);
        }
    }

    private static boolean isOneOf(Token t, TokenType... types) {
        for (TokenType type : types) {
            if (t.type == type) return true;
        }

        return false;
    }
}
