package calc.errors;

public class LexicalError extends IllegalArgumentException {
    public LexicalError(String s) {
        super(s);
    }
}
