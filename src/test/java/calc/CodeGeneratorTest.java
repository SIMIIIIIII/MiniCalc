package calc;

import org.javagrader.Grade;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Grade
public class CodeGeneratorTest {
    @Test
    @Grade(value = 1)
    public void testCodeGeneratorWithSpill(){
        String value = """
                let x = 3 + 4 * 2;
                let y = (x - 1) / 2.5;
                print y;
                """;
        CodeGenerator codeGenerator = new CodeGenerator(value);
        codeGenerator.generate(2);

        List<String> expected = new ArrayList<>(List.of("t0 = 3", "t1 = 4", "t2 = 2",
                "t3 = t1 * t2", "t4 = t0 + t3", "x = t4", "t5 = 1", "t6 = x - t5",
                "t7 = 2.5", "t8 = t6 / t7", "y = t8", "print y")
        );

        Map<String, ArrayList<String>> expectedRegister = new HashMap<>();
        expectedRegister.put("R0", new ArrayList<>(List.of("t0", "t5", "t6", "t7", "x", "t4", "t8", "y")));
        expectedRegister.put("R1", new ArrayList<>(List.of("t1", "t3")));

        List<String> expectedSpill = new ArrayList<>(List.of("t2"));

        assertEquals(expected, codeGenerator.instructions);
        assertEquals(expectedRegister, codeGenerator.registers.registers);
        assertEquals(expectedSpill, codeGenerator.registers.spill);
    }

    @Test
    @Grade(value = 1)
    public void testCodeGeneratorWithoutSpill(){
        String value = """
                let x = 3 + 4 * 2;
                let y = (x - 1) / 2.5;
                print y;
                """;
        CodeGenerator codeGenerator = new CodeGenerator(value);
        codeGenerator.generate(3);

        List<String> expected = new ArrayList<>(List.of("t0 = 3", "t1 = 4", "t2 = 2",
                "t3 = t1 * t2", "t4 = t0 + t3", "x = t4", "t5 = 1", "t6 = x - t5",
                "t7 = 2.5", "t8 = t6 / t7", "y = t8", "print y")
        );

        Map<String, ArrayList<String>> expectedRegister = new HashMap<>();
        expectedRegister.put("R0", new ArrayList<>(List.of("t0", "t5", "t6", "t7", "x", "t4", "t8", "y")));
        expectedRegister.put("R1", new ArrayList<>(List.of("t1", "t3")));
        expectedRegister.put("R2", new ArrayList<>(List.of("t2")));

        assertEquals(expected, codeGenerator.instructions);
        assertEquals(expectedRegister, codeGenerator.registers.registers);
        assertTrue(codeGenerator.registers.spill.isEmpty());
    }
}
