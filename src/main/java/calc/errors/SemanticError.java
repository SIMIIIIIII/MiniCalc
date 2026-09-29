package calc.errors;

public class SemanticError extends IllegalArgumentException {
    public SemanticError(String s) {
        super(s);
    }
}
