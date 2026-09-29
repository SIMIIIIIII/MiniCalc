package calc;

import calc.Types.VarType;
import calc.errors.SemanticError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SymboleTable {
    Map<String, VarType> symbols;
    SymboleTable parent;
    SymboleTable current;
    ScopeChecker scopeChecker;
    int line = 1;
    List<SemanticError> errors = new ArrayList<>();

    SymboleTable(SymboleTable parent) {
        this.parent = parent;
        symbols = new HashMap<>();
    }

    SymboleTable() {
        symbols = new HashMap<>();
    }

    void build(List<AST.StmtNode> nodeList) {
        enterScope();
        scopeChecker = new ScopeChecker(current);
        for (AST.StmtNode stmtNode: nodeList) {
            getVarType(stmtNode);
            line++;
        }
        if (!current.errors.isEmpty()) {
            String msg = "\nLe programme a collecté " + errors.size() + " erreur(s) avant de s'arrêter :\n" +
                    current.errors.stream()
                            .map(e -> " -> [" + e.getClass().getSimpleName() + "] : " + e.getMessage())
                            .collect(Collectors.joining("\n"));
            throw new SemanticError(msg);
        }
    }

    void getVarType(AST.StmtNode stmtNode) {
        if (stmtNode.getClass() == AST.PrintStmt.class) {
            AST.PrintStmt p = (AST.PrintStmt) stmtNode;

            try {
                scopeChecker.visitPrintStmt(p);
                getVarType(p.value);
            } catch (SemanticError e) {
                current.errors.add(e);
            }
        }
        else {
            AST.LetStmt let = (AST.LetStmt) stmtNode;
            try {
                scopeChecker.visitLetStmt(let);
                String name = let.name;
                VarType varType = getVarType(let.value);
                declare(name, varType);
            } catch (SemanticError e) {
                current.errors.add(e);
            }

        }
    }

    VarType getVarType(AST.ExprNode exprNode) {
        if (exprNode.getClass() == AST.FloatLiteral.class) return VarType.FLOAT;
        if (exprNode.getClass() == AST.IntLiteral.class) return VarType.INT;
        if (exprNode.getClass() == AST.VarRef.class) {
            AST.VarRef varRef = (AST.VarRef) exprNode;

            VarType varType = current.symbols.get(varRef.name);
            if (varType == null) {
                current.errors.add(new SemanticError("Ligne " + line + ": '" + varRef.name + "' n'est pas declare"));
                return VarType.ERROR;
            }
            return varType;
        }

        AST.BinaryOp op = (AST.BinaryOp) exprNode;

        try {
            return VarType.combine(
                    getVarType(op.left),
                    getVarType(op.right)
            );
        } catch (SemanticError e) {
            return VarType.ERROR;
        }
    }


    void enterScope() {
        current = new SymboleTable(current);
    }

    void exitScope() {
            current = current.parent;
        }

    void declare(String name, VarType type) throws SemanticError {
        if (current == null)
            throw new SemanticError("'" + name + "' doit être declaré dans un bloc");
        if (current.symbols.containsKey(name))
            throw new SemanticError("Ligne " + line + ": '" + name + "' deja declare dans cette portee");
        current.symbols.put(name, type);
    }

    void resolve(String name) throws SemanticError {
        SymboleTable s = current;
        while (s != null) {
            if (s.symbols.containsKey(name)) {
                s.symbols.get(name);
                return;
            }
            s = s.parent;
        }
        throw new SemanticError("Ligne " + line + ": '" + name + "' n'est pas declare");
    }
}
