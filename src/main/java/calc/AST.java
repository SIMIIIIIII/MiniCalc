package calc;

import calc.Types.TokenType;

import java.util.Objects;

public class AST {
    abstract static class ExprNode {
        @Override
        public String toString() {
            return super.toString();
        }

        protected String toString(int i) {
            return super.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        abstract void accept(ScopeChecker scopeChecker);

        abstract String getValue();

    }

    static class IntLiteral extends ExprNode {
        String value;
        public IntLiteral(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return "IntLiteral(" + value + ")";
        }
        public String toString(int level) {
            return toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            //if (!super.equals(o)) return false;
            IntLiteral that = (IntLiteral) o;
            return Objects.equals(value, that.value);
        }

        @Override
        void accept(ScopeChecker scopeChecker) {}

        @Override
        String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    static class FloatLiteral extends ExprNode {
        String value;
        public FloatLiteral(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return "FloatLiteral(" + value + ")";
        }
        public String toString(int level) {
            return toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            //if (!super.equals(o)) return false;
            FloatLiteral that = (FloatLiteral) o;
            return Objects.equals(value, that.value);
        }

        @Override
        void accept(ScopeChecker scopeChecker) {}

        @Override
        String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    static class VarRef extends ExprNode {
        String name;
        public VarRef(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return "Name=\"" + name + "\"";
        }
        public String toString(int level) {
            return toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            //if (!super.equals(o)) return false;
            VarRef varRef = (VarRef) o;
            return Objects.equals(name, varRef.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public void accept(ScopeChecker scopeChecker) {
            scopeChecker.symboleTable.resolve(name);
        }

        @Override
        String getValue() {
            return name;
        }
    }
    static class BinaryOp extends ExprNode {
        TokenType op;
        ExprNode left, right;

        public BinaryOp(TokenType op, ExprNode left, ExprNode right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        public String toString() {
            return "BinaryOp(" + op + ")\n" +
                    "\tleft: " + left.toString(2) + "\n" +
                     "\tright: " + right.toString(2);
        }

        public String toString(int level) {
            StringBuilder tab = new StringBuilder();
            tab.append("\t".repeat(Math.max(0, level)));
            return "BinaryOp(" + op + ")\n" +
                    tab.toString() + "left: " + left.toString(level+1) + "\n" +
                    tab.toString() + "right: " + right.toString(level+1) + "\n";
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            //if (!super.equals(o)) return false;
            BinaryOp binaryOp = (BinaryOp) o;
            return op == binaryOp.op && Objects.equals(left, binaryOp.left) && Objects.equals(right, binaryOp.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(op, left, right);
        }

        @Override
        public void accept(ScopeChecker scopeChecker) {
            left.accept(scopeChecker);
            right.accept(scopeChecker);
        }

        @Override
        String getValue() {
            return op.toString();
        }
    }

    // Hierarchie des noeuds d'instruction
    abstract static class StmtNode {
        @Override
        public String toString() {
            return super.toString();
        }

        protected String toString(int i) {
            return super.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        abstract ExprNode getValue();
        abstract String getName();
    }

    static class LetStmt extends StmtNode {
        String name;
        ExprNode value;

        public LetStmt(String name, ExprNode value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return "LetStmt (Name=\"" + name + "\")\n" +
                    "\tvalue: " + value.toString(2);
        }

        public String toString(int level){
            return "LetStmt (Name=\"" + name + "\")\n" +
                    "\t".repeat(Math.max(0, level)) + "value: " + value;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            //if (!super.equals(o)) return false;
            LetStmt letStmt = (LetStmt) o;
            return Objects.equals(name, letStmt.name) && Objects.equals(value, letStmt.value);
        }

        @Override
        ExprNode getValue() {
            return value;
        }

        @Override
        String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

    }

    static class PrintStmt extends StmtNode {
        ExprNode value;

        public PrintStmt(ExprNode value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "PrintStmt:\n" +
                    "\tvalue: " + value.toString(2) ;
        }

        public String toString(int level) {
            return "PrintStmt:\n" +
                    "\t".repeat(Math.max(0, level)) + "value: " + value ;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            //if (!super.equals(o)) return false;
            PrintStmt printStmt = (PrintStmt) o;
            return Objects.equals(value, printStmt.value);
        }

        @Override
        ExprNode getValue() {
            return value;
        }

        @Override
        String getName() {
            return "print";
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }
}