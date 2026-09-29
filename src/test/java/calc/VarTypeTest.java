package calc;

import calc.Types.VarType;
import calc.errors.SemanticError;
import org.javagrader.Grade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Grade
public class VarTypeTest {
    @Test
    @Grade(value = 1)
    public void testCombineSuccess(){
        assertEquals(VarType.INT, VarType.combine(VarType.INT, VarType.INT));
        assertEquals(VarType.FLOAT, VarType.combine(VarType.FLOAT, VarType.FLOAT));
        assertEquals(VarType.FLOAT, VarType.combine(VarType.INT, VarType.FLOAT));
        assertEquals(VarType.FLOAT, VarType.combine(VarType.FLOAT, VarType.INT));
    }

    @Test
    @Grade(value = 1)
    public void testCombineFails(){
        assertThrows(SemanticError.class, () -> VarType.combine(VarType.ERROR, VarType.ERROR));
        assertThrows(SemanticError.class, () -> VarType.combine(VarType.ERROR, VarType.FLOAT));
        assertThrows(SemanticError.class, () -> VarType.combine(VarType.ERROR, VarType.INT));
        assertThrows(SemanticError.class, () -> VarType.combine(VarType.FLOAT, VarType.ERROR));
        assertThrows(SemanticError.class, () -> VarType.combine(VarType.INT, VarType.ERROR));
    }
}
