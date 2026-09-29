package calc;

import calc.Types.TokenType;
import calc.errors.LexicalError;
import org.javagrader.Grade;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Grade
public class ScannerTest {
    @Test
    @Grade(value = 1)
    public void testSimple() {
        Scanner scanner = new Scanner("42");
        List<Token> tokens = scanner.scanAll();
        List<String> e = new ArrayList<>();

        assertFalse(tokens.isEmpty());
        assertEquals(2, tokens.size());

        assertEquals(new Token(TokenType.INT, "42", 0, 0, e), tokens.getFirst());
        assertEquals(new Token(TokenType.EOF, "", 0, 0, e), tokens.getLast());
    }

    @Test
    @Grade(value = 1)
    public void testSuccess2() {
        Scanner scanner = new Scanner("0 1 2   3 4 5 6 7   8 9");
        List<Token> tokens = scanner.scanAll();
        List<String> e = new ArrayList<>();

        assertFalse(tokens.isEmpty());
        assertEquals(11, tokens.size());

        for (int i = 0; i < 10; i++) {
            assertEquals(new Token(TokenType.INT, String.valueOf(i), 0, 0, e), tokens.get(i));
        }
        assertEquals(new Token(TokenType.EOF, "", 0, 0, e), tokens.getLast());
    }

    @Test
    @Grade(value = 1)
    public void testFloat() {
        Scanner scanner = new Scanner("3.14");
        List<Token> tokens = scanner.scanAll();
        List<String> e = new ArrayList<>();

        assertFalse(tokens.isEmpty());
        assertEquals(2, tokens.size());

        assertEquals(new Token(TokenType.FLOAT, "3.14", 0, 0, e), tokens.getFirst());
        assertEquals(new Token(TokenType.EOF, "", 0, 0, e), tokens.getLast());
    }

    @Test
    @Grade(value = 1)
    public void testFloatFailed() {
        Scanner scanner = new Scanner("3. 14");
        assertThrows(LexicalError.class, scanner::scanAll);
    }

    @Test
    @Grade(value = 1)
    public void testFloatFailed2() {
        Scanner scanner = new Scanner("3.");
        assertThrows(LexicalError.class, scanner::scanAll);
    }

    @Test
    @Grade(value = 1)
    public void testOnlyOneIdent() {
        Scanner scanner = new Scanner("print2*3;");
        List<Token> tokens = scanner.scanAll();
        List<String> e = new ArrayList<>();

        assertFalse(tokens.isEmpty());
        assertEquals(3, tokens.size());

        assertEquals(new Token(TokenType.IDENT, "print2*3", 0, 0, e), tokens.getFirst());
        assertEquals(new Token(TokenType.SEMI, ";", 0, 0, e), tokens.get(1));
        assertEquals(new Token(TokenType.EOF, "", 0, 0, e), tokens.getLast());
    }

    @Test
    @Grade(value = 1)
    public void testWrongSymbol() {
        Scanner scanner = new Scanner("let x @ 5;");
        assertThrows(LexicalError.class, scanner::scanAll);
    }

    @Test
    @Grade(value = 1)
    public void testComplexe() {
        String value = """
                let x = 3 + 4 * 2;
                let y = (x - 1) / 2.5;
                print y;
                """;
        Scanner scanner = new Scanner(value);
        List<Token> tokens = scanner.scanAll();
        List<String> e = new ArrayList<>();

        List<TokenType> expected = new ArrayList<>(List.of(
                TokenType.LET, TokenType.IDENT, TokenType.EQUALS, TokenType.INT, TokenType.PLUS, TokenType.INT,
                TokenType.STAR, TokenType.INT, TokenType.SEMI, TokenType.LET, TokenType.IDENT, TokenType.EQUALS,
                TokenType.LPAREN, TokenType.IDENT, TokenType.MINUS, TokenType.INT, TokenType.RPAREN, TokenType.SLASH,
                TokenType.FLOAT, TokenType.SEMI, TokenType.PRINT, TokenType.IDENT, TokenType.SEMI, TokenType.EOF
        ));

        List<String> expected_values = new ArrayList<>(List.of(
                "let", "x", "=", "3", "+", "4", "*", "2", ";", "let", "y", "=",
                "(", "x", "-", "1", ")", "/", "2.5", ";", "print", "y", ";", ""
        ));

        assertEquals(expected.size(), expected_values.size());
        assertFalse(tokens.isEmpty());
        assertEquals(expected.size(), tokens.size());

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), tokens.get(i).getType());
            assertEquals(expected_values.get(i), tokens.get(i).getValue());
        }
    }
}
