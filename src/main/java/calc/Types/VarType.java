package calc.Types;

import calc.AST;
import calc.errors.SemanticError;

public enum VarType {
    FLOAT, INT, ERROR;

    public static VarType combine(VarType left, VarType right) throws SemanticError {
        if (left == ERROR || right == ERROR)
            throw new SemanticError("Types incompatibles: " + left + " et " + right);
        if (left == INT && right == INT) return INT;
        return FLOAT;
    }
}
