package calc;

import calc.Types.TokenType;
import calc.errors.SyntaxError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Parser {
    List<Token> tokens;
    int pos, line;
    List<SyntaxError> errors;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        pos = 0;
        line = 1;
        errors = new ArrayList<>();
    }

    Token peek() {return pos < tokens.size() ? tokens.get(pos) : null;}
    Token advance() {
        if (pos >= tokens.size()) {
            pos++;
            return null;
        }
        Token token = tokens.get(pos++);
        if (token.getType() == TokenType.SEMI) line++;
        return token;
    }

    Token expect(TokenType type) {

        if (peek() == null || peek().getType() != type) {
            if (peek() == null) throw new SyntaxError("Le fichier est arrivé à la fin");
            throw new SyntaxError("Attendu " + type + " mais trouve " + peek().getType()  + " a la ligne " + line);
        }
        return advance();
    }

    List<AST.StmtNode> parseProgram() {
        List<AST.StmtNode> stmts = new ArrayList<>();
        while (peek().getType() != TokenType.EOF) {
            stmts.add(parseStmtWithRecovery());
        }
        if (!errors.isEmpty()) {
            String msg = "\nLe programme a collecté " + errors.size() + " erreur(s) avant de s'arrêter :\n" +
                    errors.stream()
                            .map(e -> " -> [" + e.getClass().getSimpleName() + "] : " + e.getMessage())
                            .collect(Collectors.joining("\n"));

            throw new SyntaxError(msg);
        }

        if (stmts.isEmpty()) throw new SyntaxError("Le fichier est vide");

        return stmts;
    }

    AST.StmtNode parseStmtWithRecovery() {
        try {
            return parseStmt();
        } catch (SyntaxError e) {
            errors.add(e);
            while (peek().getType() != TokenType.SEMI && peek().getType() != TokenType.EOF) advance();
            if (peek().getType() == TokenType.SEMI) advance();
            return null;
        }
    }

    AST.StmtNode parseStmt() {
        if (peek().getType() == TokenType.LET) {
            advance();
            Token name = expect(TokenType.IDENT);
            expect(TokenType.EQUALS);
            AST.ExprNode value = parseExpr();
            expect(TokenType.SEMI);
            return new AST.LetStmt(name.getValue(), value);
        } else if (peek().getType() == TokenType.PRINT) {
            advance();
            AST.ExprNode value = parseExpr();
            expect(TokenType.SEMI);
            return new AST.PrintStmt(value);
        } else {
            throw new SyntaxError("Instruction attendue, trouve " + peek().getType() + " à la ligne" + line);
        }
    }

    AST.ExprNode parseExpr() {
        AST.ExprNode left = parseTerm();
        return parseExprPrime(left);
    }

    AST.ExprNode parseExprPrime(AST.ExprNode left) {
        while (peek().getType() == TokenType.PLUS || peek().getType() == TokenType.MINUS) {
            Token op = advance();
            AST.ExprNode right = parseTerm();
            left = new AST.BinaryOp(op.getType(), left, right);
        }
        return left;
    }

    AST.ExprNode parseTerm() {
        AST.ExprNode left = parseFactor();
        while (peek().getType() == TokenType.STAR || peek().getType() == TokenType.SLASH) {
            Token op = advance();
            AST.ExprNode right = parseFactor();
            left = new AST.BinaryOp(op.getType(), left, right);
        }
        return left;
    }

    AST.ExprNode parseFactor() {
        Token t = peek();
        switch (t.getType()) {
            case INT:
                advance(); return new AST.IntLiteral(t.getValue());
            case FLOAT: advance(); return new AST.FloatLiteral(t.getValue());
            case IDENT: advance(); return new AST.VarRef(t.getValue());
            case LPAREN:
                advance();
                AST.ExprNode inner = parseExpr();
                expect(TokenType.RPAREN);
                return inner;
            default:
                throw new SyntaxError("Expression attendue, trouve " + t.getType() + " à la ligne" + line);
        }
    }
}
