package calc;

import calc.Types.TokenType;
import calc.errors.SyntaxError;
import org.javagrader.Grade;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Grade
public class ParserTest {
    @Test
    @Grade(value = 1)
    public void testPeak() {
        Scanner scanner = new Scanner("let x = 2");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);
        Token token = parser.peek();

        assertEquals(TokenType.LET, token.getType());
        assertEquals("let", token.getValue());
    }

    @Test
    @Grade(value = 1)
    public void testAdvance() {
        Scanner scanner = new Scanner("let;");
        List<Token> tokens = scanner.scanAll();

        Parser parser = new Parser(tokens);
        assertEquals(1, parser.line);

        Token token = parser.advance();
        assertEquals(TokenType.LET, token.getType());
        assertEquals("let", token.getValue());

        Token token2 = parser.peek();
        assertEquals(TokenType.SEMI, token2.getType());
        assertEquals(";", token2.getValue());

        Token token3 = parser.advance();
        assertEquals(TokenType.SEMI, token3.getType());
        assertEquals(";", token3.getValue());
        assertEquals(2, parser.line);

        Token token4 = parser.peek();
        assertEquals(TokenType.EOF, token4.getType());
        assertEquals("", token4.getValue());
    }

    @Test
    @Grade(value = 1)
    public void testPeakAndAdvanceNull() {
        Scanner scanner = new Scanner("let");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);
        parser.advance();
        parser.advance();

        assertNull(parser.advance());
        assertNull(parser.peek());
        assertThrows(SyntaxError.class, () -> parser.expect(TokenType.IDENT));
    }

    @Test
    @Grade(value = 1)
    public void testExpectSuccess() {
        Scanner scanner = new Scanner("let x = 2");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);
        Token token = parser.peek();

        assertEquals(token, parser.expect(TokenType.LET));

        Token token2 = parser.peek();
        assertEquals(TokenType.IDENT, token2.getType());
        assertEquals("x", token2.getValue());
    }

    @Test
    @Grade(value = 1)
    public void testExpectFails() {
        Scanner scanner = new Scanner("let x = 2");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertThrows(SyntaxError.class,() -> parser.expect(TokenType.IDENT));

        Token token = parser.peek();
        assertEquals(TokenType.LET, token.getType());
        assertEquals("let", token.getValue());
    }

    @Test
    @Grade(value = 1)
    public void testParseFactorSuccess() {
        Scanner scanner = new Scanner("x = 2");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertEquals(new AST.VarRef("x"), parser.parseFactor());
        Token token = parser.peek();
        assertEquals(TokenType.EQUALS, token.getType());
        assertEquals("=", token.getValue());
    }

    @Test
    @Grade(value = 1)
    public void testParseFactorParSuccess() {
        Scanner scanner = new Scanner("(x + 3);");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        AST.BinaryOp plusOp = new AST.BinaryOp(
                TokenType.PLUS,
                new AST.VarRef("x"),
                new AST.IntLiteral("3")
        );

        assertEquals(plusOp, parser.parseFactor());
        Token token = parser.peek();
        assertEquals(TokenType.SEMI, token.getType());
        assertEquals(";", token.getValue());
    }

    @Test
    @Grade(value = 1)
    public void testParseFactorParFails() {
        Scanner scanner = new Scanner("(x + 3;");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertThrows(SyntaxError.class,  parser::parseFactor);
        Token token = parser.peek();
        assertEquals(TokenType.SEMI, token.getType());
        assertEquals(";", token.getValue());
    }

    @Test
    @Grade(value = 1)
    public void testParseFactorFails() {
        Scanner scanner = new Scanner("let x = 2");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertThrows(SyntaxError.class,  parser::parseFactor);
        Token token = parser.peek();
        assertEquals(TokenType.LET, token.getType());
        assertEquals("let", token.getValue());
    }

    @Test
    @Grade(value = 1)
    public void testParseTermSimple() {
        Scanner scanner = new Scanner("x");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);
        assertEquals(new AST.VarRef("x"), parser.parseTerm());
    }

    @Test
    @Grade(value = 1)
    public void testParseSimple2() {
        Scanner scanner = new Scanner("x * 3");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        AST.BinaryOp mulOp = new AST.BinaryOp(
                TokenType.STAR,
                new AST.VarRef("x"),
                new AST.IntLiteral("3")
        );
        assertEquals(mulOp, parser.parseTerm());
    }

    @Test
    @Grade(value = 1)
    public void testParseTerm() {
        Scanner scanner = new Scanner("3 / 2.3 * x");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        AST.BinaryOp mulOp = new AST.BinaryOp(
                TokenType.STAR,
                new AST.BinaryOp(
                        TokenType.SLASH,
                        new AST.IntLiteral("3"),
                        new AST.FloatLiteral("2.3")
                ),
                new AST.VarRef("x")
        );
        assertEquals(mulOp, parser.parseTerm());
    }

    @Test
    @Grade(value = 1)
    public void testParseExprPrimeSimple() {
        Scanner scanner = new Scanner("* 2.3");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);
        AST.IntLiteral int8 = new AST.IntLiteral("8");
        assertEquals(int8, parser.parseExprPrime(int8));
    }

    @Test
    @Grade(value = 1)
    public void testParseExprPrime() {
        Scanner scanner = new Scanner("+ 2.3");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);
        AST.IntLiteral int8 = new AST.IntLiteral("8");
        AST.BinaryOp plusOp = new AST.BinaryOp(
                TokenType.PLUS,
                int8,
                new AST.FloatLiteral("2.3")
        );
        assertEquals(plusOp, parser.parseExprPrime(int8));
    }

    @Test
    @Grade(value = 1)
    public void testParseExprSimple() {
        Scanner scanner = new Scanner("2.3");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);
        assertEquals(new AST.FloatLiteral("2.3"), parser.parseExpr());
    }

    @Test
    @Grade(value = 1)
    public void testParseExpr() {
        Scanner scanner = new Scanner("2 / 2.3");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertEquals(
                new AST.BinaryOp(
                        TokenType.SLASH,
                        new AST.IntLiteral("2"),
                        new AST.FloatLiteral("2.3")
                ),
                parser.parseExpr()
        );
    }

    @Test
    @Grade(value = 1)
    public void testParseExprPar() {
        Scanner scanner = new Scanner("(2 + 3) * 4");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertEquals(
                new AST.BinaryOp(
                        TokenType.STAR,
                        new AST.BinaryOp(
                                TokenType.PLUS,
                                new AST.IntLiteral("2"),
                                new AST.IntLiteral("3")
                        ),
                        new AST.IntLiteral("4")
                ),
                parser.parseExpr()
        );
    }

    @Test
    @Grade(value = 1)
    public void testParseStmtLet() {
        Scanner scanner = new Scanner("let x = 2;");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertEquals(
                new AST.LetStmt(
                        "x",
                        new AST.IntLiteral("2")
                ),
                parser.parseStmt()
        );
    }

    @Test
    @Grade(value = 1)
    public void testParseStmtPrint() {
        Scanner scanner = new Scanner("print x;");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertEquals(
                new AST.PrintStmt(new AST.VarRef("x")),
                parser.parseStmt()
        );
    }

    @Test
    @Grade(value = 1)
    public void testParseStmtFails() {
        Scanner scanner = new Scanner("x;");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);
        assertThrows(SyntaxError.class, parser::parseStmt);
    }

    @Test
    @Grade(value = 1)
    public void testParseStmtWithRecovery() {
        Scanner scanner = new Scanner("let x 3;");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertTrue(parser.errors.isEmpty());
        assertNull(parser.parseStmtWithRecovery());
        assertEquals(1, parser.errors.size());
        assertEquals(2, parser.line);
    }

    @Test
    @Grade(value = 1)
    public void testParseProgramSuccess() {
        String value = """
                let x = 3 + 4 * 2;
                let y = (x - 1) / 2.5;
                print y;
                """;
        Scanner scanner = new Scanner(value);
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        List<AST.StmtNode> stmtNodeList = parser.parseProgram();

        List<AST.StmtNode> expected = new ArrayList<>(List.of(
                new AST.LetStmt(
                        "x",
                        new AST.BinaryOp(
                                TokenType.PLUS,
                                new AST.IntLiteral("3"),
                                new AST.BinaryOp(
                                        TokenType.STAR,
                                        new AST.IntLiteral("4"),
                                        new AST.IntLiteral("2")
                                )
                        )
                ),
                new AST.LetStmt(
                        "y",
                        new AST.BinaryOp(
                                TokenType.SLASH,
                                new AST.BinaryOp(
                                        TokenType.MINUS,
                                        new AST.VarRef("x"),
                                        new AST.IntLiteral("1")
                                ),
                                new AST.FloatLiteral("2.5")
                        )
                ),
                new AST.PrintStmt(new AST.VarRef("y"))
        ));

        assertEquals(expected, stmtNodeList);
    }

    @Test
    @Grade(value = 1)
    public void testParseProgramFails() {
        String value = """
                let x = 3 + 4 2;
                let y = (x - 1 / 2.5;
                print y
                """;
        Scanner scanner = new Scanner(value);
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);

        assertEquals(0, parser.errors.size());
        assertThrows(SyntaxError.class, parser::parseProgram);
        assertEquals(3, parser.errors.size());
    }

    @Test
    @Grade(value = 1)
    public void testParseEmptyProgram() {
        Scanner scanner = new Scanner("");
        List<Token> tokens = scanner.scanAll();
        Parser parser = new Parser(tokens);
        assertThrows(SyntaxError.class, parser::parseProgram);
    }
}