package calc;

import calc.Types.VarType;
import org.javagrader.Grade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Grade
public class SymboleTableCheckerTest {
    @Test
    @Grade(value = 1)
    public void testVisitFloatLiteral() {
        SymboleTable symboleTable = new SymboleTable(null);
        ScopeChecker scopeChecker = new ScopeChecker(symboleTable);
        scopeChecker.visitFloatLiteral(new AST.FloatLiteral("0.5"));
        assertTrue(scopeChecker.errors.isEmpty());
    }

    @Test
    @Grade(value = 1)
    public void testVisitIntLiteral() {
        SymboleTable symboleTable = new SymboleTable(null);
        ScopeChecker scopeChecker = new ScopeChecker(symboleTable);
        scopeChecker.visitIntLiteral(new AST.IntLiteral("5"));
        assertTrue(scopeChecker.errors.isEmpty());
    }

    @Test
    @Grade(value = 1)
    public void testVisitVarRefSuccess() {
        SymboleTable symboleTable = new SymboleTable(null);
        ScopeChecker scopeChecker = new ScopeChecker(symboleTable);
        assertTrue(scopeChecker.errors.isEmpty());

        symboleTable.enterScope();
        symboleTable.declare("x", VarType.FLOAT);

        scopeChecker.visitVarRef(new AST.VarRef("x"));
        assertTrue(scopeChecker.errors.isEmpty());
    }

    @Test
    @Grade(value = 1)
    public void testVisitVarRefFails() {
        SymboleTable symboleTable = new SymboleTable(null);
        ScopeChecker scopeChecker = new ScopeChecker(symboleTable);
        assertTrue(scopeChecker.errors.isEmpty());

        symboleTable.enterScope();
        scopeChecker.visitVarRef(new AST.VarRef("x"));
        assertFalse(scopeChecker.errors.isEmpty());
    }

    @Test
    @Grade
    public void testVisitPrintStmtSuccess() {
        SymboleTable symboleTable = new SymboleTable(null);
        ScopeChecker scopeChecker = new ScopeChecker(symboleTable);
        assertTrue(scopeChecker.errors.isEmpty());

        symboleTable.enterScope();
        symboleTable.declare("x", VarType.FLOAT);

        scopeChecker.visitPrintStmt(new AST.PrintStmt(new AST.VarRef("x")));
        assertTrue(scopeChecker.errors.isEmpty());
    }

    @Test
    @Grade
    public void testVisitPrintStmtFails() {
        SymboleTable symboleTable = new SymboleTable(null);
        ScopeChecker scopeChecker = new ScopeChecker(symboleTable);
        assertTrue(scopeChecker.errors.isEmpty());

        symboleTable.enterScope();

        scopeChecker.visitPrintStmt(new AST.PrintStmt(new AST.VarRef("x")));
        assertFalse(scopeChecker.errors.isEmpty());
    }

    @Test
    @Grade
    public void testVisitLetStmtFails() {
        SymboleTable symboleTable = new SymboleTable(null);
        ScopeChecker scopeChecker = new ScopeChecker(symboleTable);
        assertTrue(scopeChecker.errors.isEmpty());

        symboleTable.enterScope();

        scopeChecker.visitLetStmt(new AST.LetStmt("x", new AST.FloatLiteral("2.3")));
        assertFalse(scopeChecker.errors.isEmpty());
    }

    @Test
    @Grade
    public void testVisitLetStmtSuccess() {
        SymboleTable symboleTable = new SymboleTable(null);
        ScopeChecker scopeChecker = new ScopeChecker(symboleTable);
        assertTrue(scopeChecker.errors.isEmpty());

        symboleTable.enterScope();

        scopeChecker.visitLetStmt(new AST.LetStmt("x", new AST.FloatLiteral("2.3")));
        assertTrue(scopeChecker.errors.isEmpty());
    }
}
