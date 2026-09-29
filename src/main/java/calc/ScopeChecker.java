package calc;

import calc.errors.SemanticError;

import java.util.ArrayList;
import java.util.List;

public class ScopeChecker implements Visitor.StmtVisitor<Void>, Visitor.ExprVisitor<Void> {
    SymboleTable symboleTable;
    List<SemanticError> errors;

    ScopeChecker(SymboleTable symboleTable) {
        this.symboleTable = symboleTable;
        errors = new ArrayList<>();
    }

    public Void visitLetStmt(AST.LetStmt n) {
        try {
            n.value.accept(this);
        } catch (SemanticError e) {
            errors.add(e);
        }
        return null;
    }

    public Void visitPrintStmt(AST.PrintStmt n) {
        try {
            n.value.accept(this);
        }catch (SemanticError e) {
            errors.add(e);
        }
        return null;
    }

    public Void visitVarRef(AST.VarRef n) {
        try {
            symboleTable.resolve(n.name);
        }
        catch (SemanticError e) {
            errors.add(e);
        }
        return null;
    }
    public Void visitBinaryOp(AST.BinaryOp n) {
        n.left.accept(this);
        n.right.accept(this);
        return null;
    }

    public Void visitIntLiteral(AST.IntLiteral n) { return null;}
    public Void visitFloatLiteral(AST.FloatLiteral n) { return null;}
}
