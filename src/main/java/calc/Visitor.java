package calc;

public interface Visitor {
    interface ExprVisitor<T> {
        T visitIntLiteral(AST.IntLiteral n);
        T visitFloatLiteral(AST.FloatLiteral n);
        T visitVarRef(AST.VarRef n);
        T visitBinaryOp(AST.BinaryOp n);
    }

    interface StmtVisitor<T> {
        T visitLetStmt(AST.LetStmt n);
        T visitPrintStmt(AST.PrintStmt n);
    }
}
