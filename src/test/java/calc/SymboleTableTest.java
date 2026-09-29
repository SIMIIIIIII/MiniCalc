package calc;

import calc.Types.VarType;
import calc.errors.SemanticError;
import org.javagrader.Grade;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Grade
public class SymboleTableTest {
    @Test
    @Grade(value = 1)
    public void testCreateScope() {
        SymboleTable symboleTable = new SymboleTable();
        assertNull(symboleTable.current);
        assertNull(symboleTable.parent);
        assertTrue(symboleTable.symbols.isEmpty());
    }

    @Test
    @Grade(value = 1)
    public void testScopeEnter() {
        SymboleTable symboleTable = new SymboleTable();
        symboleTable.enterScope();

        assertNotNull(symboleTable.current);
        assertNull(symboleTable.parent);
    }

    @Test
    @Grade(value = 1)
    public void testScopeExit() {
        SymboleTable symboleTable = new SymboleTable();
        symboleTable.enterScope();

        assertNotNull(symboleTable.current);
        assertNull(symboleTable.parent);

        symboleTable.exitScope();
        assertNull(symboleTable.current);
        assertNull(symboleTable.parent);
        assertTrue(symboleTable.symbols.isEmpty());
    }

    @Test
    @Grade(value = 1)
    public void testScopeDeclareSuccess() {
        SymboleTable symboleTable = new SymboleTable();
        symboleTable.enterScope();

        assertTrue(symboleTable.symbols.isEmpty());
        assertTrue(symboleTable.current.symbols.isEmpty());

        symboleTable.declare("x", VarType.INT);
        symboleTable.declare("z", VarType.FLOAT);

        assertFalse(symboleTable.current.symbols.isEmpty());
        assertEquals(VarType.INT, symboleTable.current.symbols.get("x"));
        assertEquals(VarType.FLOAT, symboleTable.current.symbols.get("z"));
    }

    @Test
    @Grade(value = 1)
    public void testScopeDeclareChildSuccess() {
        SymboleTable symboleTable = new SymboleTable();
        symboleTable.enterScope();

        assertTrue(symboleTable.symbols.isEmpty());
        assertTrue(symboleTable.current.symbols.isEmpty());

        symboleTable.declare("x", VarType.INT);
        assertFalse(symboleTable.current.symbols.isEmpty());
        assertEquals(VarType.INT, symboleTable.current.symbols.get("x"));

        symboleTable.current.enterScope();
        assertTrue(symboleTable.current.current.symbols.isEmpty());
        symboleTable.current.declare("x", VarType.INT);
        assertFalse(symboleTable.current.current.symbols.isEmpty());
        assertEquals(VarType.INT, symboleTable.current.current.symbols.get("x"));
    }

    @Test
    @Grade(value = 1)
    public void testScopeDeclareNoEnterFailed() {
        SymboleTable symboleTable = new SymboleTable();
        assertThrows(SemanticError.class, () -> symboleTable.declare("x", VarType.FLOAT));
    }

    @Test
    @Grade(value = 1)
    public void testScopeDeclareSameScopeFailed() {
        SymboleTable symboleTable = new SymboleTable();
        symboleTable.enterScope();
        symboleTable.declare("x", VarType.INT);

        assertFalse(symboleTable.current.symbols.isEmpty());
        assertEquals(VarType.INT, symboleTable.current.symbols.get("x"));
        assertThrows(SemanticError.class, () -> symboleTable.declare("x", VarType.FLOAT));
    }

    @Test
    @Grade(value = 1)
    public void buildSuccess() {
        String value = """
                let x = 3 + 4 * 2;
                let y = (x - 1) / 2.5;
                let s = 8.6;
                let k = x / s;
                print s + y;
                """;
        Scanner scanner = new Scanner(value);
        List<Token> tokens = scanner.scanAll();

        Parser parser = new Parser(tokens);
        List<AST.StmtNode> stmtNodeList= parser.parseProgram();

        SymboleTable symboleTable = new SymboleTable();

        Map<String, VarType> expected = new HashMap<>();
        expected.put("s", VarType.FLOAT);
        expected.put("x", VarType.INT);
        expected.put("k", VarType.FLOAT);
        expected.put("y", VarType.FLOAT);

        symboleTable.build(stmtNodeList);
        assertEquals(expected, symboleTable.current.symbols);
        assertTrue(symboleTable.current.errors.isEmpty());
    }

    @Test
    @Grade(value = 1)
    public void buildFails() {
        String value = """
                let x = 3 + 4 * 2;
                let y = (x - 1) / 2.5;
                let k = h / s;
                print s + y;
                """;
        Scanner scanner = new Scanner(value);
        List<Token> tokens = scanner.scanAll();

        Parser parser = new Parser(tokens);
        List<AST.StmtNode> stmtNodeList= parser.parseProgram();

        SymboleTable symboleTable = new SymboleTable();
        assertThrows(SemanticError.class, () -> symboleTable.build(stmtNodeList));

        assertFalse(symboleTable.current.errors.isEmpty());
        assertEquals(3, symboleTable.current.errors.size());
    }

}
