package calc;

import calc.Types.TokenType;
import calc.errors.LexicalError;
import org.javagrader.Grade;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Grade
public class TokenTest {
    @Test
    @Grade(value = 1)
    public void testSimple() {
        List<String> e = new ArrayList<>();
        Token token = new Token(TokenType.LET, "let", 0, 0, e);
        assertEquals(TokenType.LET, token.getType());
        assertEquals("let", token.getValue());
    }

    public void testInvalid() {
        List<String> e = new ArrayList<>();
        assertThrows(LexicalError.class, () -> new Token(TokenType.LET, "invalid", 0, 0, e));
    }
}