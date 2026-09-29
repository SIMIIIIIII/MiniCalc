package calc;

import calc.Types.TokenType;

import java.util.*;

public class Registers {
    List<String> instructions;
    List<String> spill;
    List<List<String>> liveness;
    Map<String, ArrayList<String>> registers, graph;

    Registers(List<String> instructions) {
        this.instructions = instructions;
    }

    void compute(int k) {
        registers = new HashMap<>();
        graph = new HashMap<>();
        liveness = new ArrayList<>();
        spill = new ArrayList<>();

        calculateVivacity();
        constructGraph();
        Map<String, ArrayList<String>> tempRegister = coloration();

        List<Map.Entry<String, ArrayList<String>>> orderedTemp = tempRegister.entrySet()
                .stream()
                .sorted(new Comparator<Map.Entry<String, ArrayList<String>>>() {
                    @Override
                    public int compare(Map.Entry<String, ArrayList<String>> o1, Map.Entry<String, ArrayList<String>> o2) {
                        return o2.getValue().size() - o1.getValue().size();
                    }
                })
                .toList();

        for (int i = 0; i < orderedTemp.size(); i++) {
            if (i < k) {
                registers.put("R" + i, orderedTemp.get(i).getValue());
                continue;
            }
            spill.addAll(orderedTemp.get(i).getValue());
        }

    }

    void calculateVivacity(){
        Scanner scanner = new Scanner(String.join(";\n", instructions));
        List<Token> tokens = scanner.scanAll();
        liveness = new ArrayList<>();

        Map<String, Integer[]> temp = new HashMap<>();

        List<String> vars = new ArrayList<>();
        int line = 0;

        for (Token token : tokens) {
            if (token.getType() == TokenType.SEMI) {
                line++;
                liveness.add(new ArrayList<>());
                continue;
            }
            if (token.getType() == TokenType.IDENT ) {
                if (temp.containsKey(token.getValue())){
                    int finalLine = line;
                    temp.compute(token.getValue(), (k, tab) -> new Integer[]{tab[0], finalLine});
                }
                else {
                    temp.put(token.getValue(), new Integer[]{line, line});
                }
            }
        }

        for (String key : temp.keySet()) {
            Integer[] v = temp.get(key);

            for (int i = v[0]; i < Math.max(1, v[1]); i++) {
                List<String> l = liveness.get(i);
                l.add(key);
            }
        }

        liveness.sort(new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                return o2.size() - o1.size();
            }
        });
    }

    void constructGraph() {
        for (List<String> varsList : liveness) {
            for (int i = 0; i < varsList.size(); i++) {
                if (!graph.containsKey(varsList.get(i)))
                    graph.put(varsList.get(i), new ArrayList<>());

                for (int j = i+1; j < varsList.size(); j++) {
                    if (!graph.containsKey(varsList.get(j)))
                        graph.put(varsList.get(j), new ArrayList<>());

                    if (!graph.get(varsList.get(i)).contains(varsList.get(j)))
                        graph.get(varsList.get(i)).add(varsList.get(j));

                    if (!graph.get(varsList.get(j)).contains(varsList.get(i)))
                        graph.get(varsList.get(j)).add(varsList.get(i));
                }
            }
        }
    }

    Map<String, ArrayList<String>> coloration() {
        List<String> used = new ArrayList<>();
        Map<String, ArrayList<String>> registers = new HashMap<>();


        List<String> keys = graph
                .entrySet()
                .stream()
                .sorted(new Comparator<Map.Entry<String, ArrayList<String>>>() {
                    @Override
                    public int compare(Map.Entry<String, ArrayList<String>> o1, Map.Entry<String, ArrayList<String>> o2) {
                        return o2.getValue().size() - o1.getValue().size();
                    }
                })
                .map(Map.Entry::getKey)
                .toList();

        for (String key : keys){
            if (used.contains(key)) continue;

            if (registers.isEmpty()) {
                registers.put(key, new ArrayList<>(List.of(key)));
                used.add(key);
                continue;
            }

            boolean put = false;
            for (String registerKey: registers.keySet()) {
                if (!graph.get(key).contains(registerKey)) {
                    registers.get(registerKey).add(key);
                    put = true;
                    break;
                }
            }

            if (!put) registers.put(key, new ArrayList<>(List.of(key)));
        }
        return registers;
    }
}
