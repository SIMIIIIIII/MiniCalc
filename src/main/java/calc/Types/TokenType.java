package calc.Types;

public enum TokenType {
    LET {
        @Override
        public boolean verify(String value) {return value.equals("let");}

        @Override
        public String toString() {return "let";}
    },
    PRINT {
        @Override
        public boolean verify(String value) {return value.equals("print");}

        @Override
        public String toString() {return "print";}
    },
    IDENT {
        @Override
        public boolean verify(String value) {
            if (value.isBlank()) return false;
            char c = value.charAt(0);
            return Character.isLetter(c);
        }
    },
    INT {
        @Override
        public boolean verify(String value) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException _) {
                return false;
            }
        }
    },
    FLOAT {
        @Override
        public boolean verify(String value) {
            try {
                Float.parseFloat(value);
                return true;
            } catch (NumberFormatException _) {
                return false;
            }
        }
    },
    PLUS {
        @Override
        public boolean verify(String value) {return value.equals("+");}

        @Override
        public String toString() {return "+";}
    },
    MINUS {
        @Override
        public boolean verify(String value) {return value.equals("-");}

        @Override
        public String toString() {return "-";}
    },
    STAR {
        @Override
        public boolean verify(String value) {return value.equals("*");}

        @Override
        public String toString() {return "*";}
    },
    SLASH {
        @Override
        public boolean verify(String value) {return value.equals("/");}

        @Override
        public String toString() {return "/";}
    },
    EQUALS {
        @Override
        public boolean verify(String value) {return value.equals("=");}

        @Override
        public String toString() {return "=";}
    },
    LPAREN {
        @Override
        public boolean verify(String value) {return value.equals("(");}

        @Override
        public String toString() {return "(";}
    },
    RPAREN {
        @Override
        public boolean verify(String value) {return value.equals(")");}

        @Override
        public String toString() {return ")";}
    },

    SEMI {
        @Override
        public boolean verify(String value) {
            return value.equals(";");
        }

        @Override
        public String toString() {return ";";}
    },
    EOF {
        @Override
        public boolean verify(String value) {
            return value.isEmpty();
        }
    },
    ERROR {
        @Override
        public boolean verify(String value) {
            return true;
        }
    };

    public abstract boolean verify(String value);

    @Override
    public String toString() {
        return super.toString();
    }
}
