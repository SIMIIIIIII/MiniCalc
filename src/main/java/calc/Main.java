package calc;

import calc.Types.VarType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String value = """
                let x = 3 + 4 * 2;
                let y = (x - 1) / 2.5;
                print y;
                """;
        CodeGenerator codeGenerator = new CodeGenerator(value);
        codeGenerator.generate(2);

        System.out.println(codeGenerator.instructions);
        System.out.println(codeGenerator.registers.registers);
        System.out.println(codeGenerator.registers.spill);
    }
}