package calc.errors;

public class SyntaxError extends IllegalArgumentException {
    public SyntaxError(String s) {
        super(s);
    }
}
