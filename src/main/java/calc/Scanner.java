package calc;

import calc.Types.TokenType;
import calc.errors.LexicalError;

import java.util.ArrayList;
import java.util.List;


public class Scanner {
    private final String source;
    private int pos;
    private int line;
    private int col;
    private final List<Token> tokens;
    protected final List<String> errorMsg;

    public Scanner(String text) {
        source = text;
        pos = 0;
        line = 1;
        col = 0;
        tokens = new ArrayList<>();
        errorMsg = new ArrayList<>();
    }

    public List<Token> scanAll() {
        while (pos < source.length()) {
            skipWhitespace();
            if (pos >= source.length()) break;

            char c = source.charAt(pos);
            if (Character.isDigit(c)) tokens.add(scanNumber());
            else if (Character.isLetter(c)) tokens.add(scanIdentOrKeyword());
            else tokens.add(scanOperatorOrPunct());
        }
        tokens.add(new Token(TokenType.EOF, "", line, col, errorMsg));

        if (!errorMsg.isEmpty()) {
            StringBuilder str = new StringBuilder("\n");
            for (String msg : errorMsg) {
                str.append(msg);
            }
            throw new LexicalError(str.toString());
        }

        return tokens;
    }

    private void skipWhitespace() {
        while (pos < source.length()) {
            char c = source.charAt(pos);
            if (Character.isSpaceChar(c) || c == '\t') {
                pos++;
                col++;
            }
            else if (c == '\n') {
                pos++;
                line++;
                col = 0;
            }
            else break;
        }
    }

    private Token scanNumber() {
        int start = pos;
        int save = col;
        while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
            pos++;
            col++;
        }

        //partie décimale ?
        boolean isFloat = false;
        if (pos < source.length() && source.charAt(pos) == '.') {
            isFloat = true;
            pos++;
            col++;
            int oldPos = pos;
            while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
                pos++;
                col++;
            };
            if (oldPos == pos) errorMsg.add("Ligne " + line + ":" + col + " => Caractère inattendu '.'\n");
        }

        String text = source.substring(start, pos);
        return isFloat ? new Token(TokenType.FLOAT, text, line, save, errorMsg) : new Token(TokenType.INT, text, line, save, errorMsg);
    }

    private Token scanIdentOrKeyword() {
        int start = pos;
        int save = col;
        while (pos < source.length() && (!Character.isWhitespace(source.charAt(pos)) && source.charAt(pos) != ';')) {
            pos++;
            col++;
        }

        String text = source.substring(start, pos);
        if (text.equals("let")) return new Token(TokenType.LET, text, line, save, errorMsg);
        if (text.equals("print")) return new Token(TokenType.PRINT, text, line, save, errorMsg);
        return new Token(TokenType.IDENT, text, line, save, errorMsg);
    }


    private Token scanOperatorOrPunct() {
        char c = source.charAt(pos);
        pos++;
        col++;
        return switch (c) {
            case '+' -> new Token(TokenType.PLUS, "+", line, col - 1, errorMsg);
            case '-' -> new Token(TokenType.MINUS, "-", line, col - 1, errorMsg);
            case '*' -> new Token(TokenType.STAR, "*", line, col - 1, errorMsg);
            case '/' -> new Token(TokenType.SLASH, "/", line, col - 1, errorMsg);
            case '=' -> new Token(TokenType.EQUALS, "=", line, col - 1, errorMsg);
            case '(' -> new Token(TokenType.LPAREN, "(", line, col - 1, errorMsg);
            case ')' -> new Token(TokenType.RPAREN, ")", line, col - 1, errorMsg);
            case ';' -> new Token(TokenType.SEMI, ";", line, col - 1, errorMsg);
            default -> {
                errorMsg.add("Ligne " + line + ":" + col + " => Caractère inattendu '" + c + "'\n");
                yield new Token(TokenType.ERROR, "", line, col - 1, errorMsg);
            }
        };
    }
}
