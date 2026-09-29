package calc;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    List<String> instructions = new ArrayList<>();
    int tempCounter = 0;
    List<AST.StmtNode> listAST;
    ScopeChecker scopeChecker;
    String code;
    Scanner scanner;
    List<Token> tokens;
    Parser parser;
    SymboleTable symboleTable;
    Registers registers;

    CodeGenerator(String code) {
        this.code = code;
    }

    void generate(int k) {
        scanner = new Scanner(code);
        tokens = scanner.scanAll();
        parser = new Parser(tokens);
        listAST = parser.parseProgram();
        symboleTable = new SymboleTable();
        symboleTable.build(listAST);
        scopeChecker = new ScopeChecker(symboleTable);

        for (AST.StmtNode stmtNode : listAST) generate(stmtNode);

        registers = new Registers(instructions);
        registers.compute(k);
    }

    String generate(AST.ExprNode exprNode) {
        if (exprNode.getClass() == AST.FloatLiteral.class || exprNode.getClass() == AST.IntLiteral.class) {

            String t = newTemp();
            instructions.add(t + " = " + exprNode.getValue());
            return t;
        }

        if (exprNode.getClass() == AST.VarRef.class) return exprNode.getValue();

        AST.BinaryOp op = (AST.BinaryOp) exprNode;
        String left = generate(op.left);
        String right = generate(op.right);

        String t = newTemp();
        instructions.add(t  + " = " + left + " " + op.getValue() + " " + right);
        return t;
    }

    void generate(AST.StmtNode stmtNode) {
        String name = stmtNode.getName();
        String equals = name.equals("print") ? "" : "=";
        String t = name + " " + equals + " ";
        instructions.add(t.trim() + " " + generate(stmtNode.getValue()));
    }

    String newTemp() { return "t" + (tempCounter++); }

    /**
    @Override
    public String visitIntLiteral(AST.IntLiteral n) {
        String t = newTemp();
        instructions.add(t + " = " + n.value);
        return t;
    }

    @Override
    public String visitFloatLiteral(AST.FloatLiteral n) {
        String t = newTemp();
        instructions.add(t + " = " + n.value);
        return t;
    }

    @Override
    public String visitVarRef(AST.VarRef n) {
        return n.name;
    }

    @Override
    public String visitBinaryOp(AST.BinaryOp n) {
        String leftTemp = n.left.accept(this);
        String rightTemp = n.right.accept(this);
        String resultTemp = newTemp();
        instructions.add(resultTemp + " = " + leftTemp + " "
                + n.op.toString() + " " + rightTemp); // (5) combine les deux
        return resultTemp;
    }

    @Override
    public Void visitLetStmt(AST.LetStmt n) {
        String valueTemp = n.value.accept(this.scopeChecker);
        instructions.add(n.name + " = " + valueTemp);
        return null;
    }

    @Override
    public Void visitPrintStmt(AST.PrintStmt n) {
        String valueTemp = n.value.accept(this);
        instructions.add("print " + valueTemp);
        return null;
    } */
}
