package calc;

import calc.Types.TokenType;

import java.util.List;
import java.util.Objects;

public class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value, int line, int col, List<String> errorMsg) {
        if (!type.verify(value)) {
            String s = value.isEmpty() ? "" : value.substring(0, 1);
            errorMsg.add("Ligne " + line + ":" + col + " => Caractère inattendu '" + s + "'\n");;
            this.type = TokenType.ERROR;
            this.value = "";
        }
        else {
            this.type = type;
            this.value = value;
        }
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type && Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
