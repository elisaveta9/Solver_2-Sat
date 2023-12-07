package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class ConjunctiveNormalForm {

    private final HashMap<String, HashSet<Integer>> literals = new HashMap<>();
    private final HashMap<Integer, Pair> pairHashMap = new HashMap<>();
    private Integer idDisjunction = 0;

    public ConjunctiveNormalForm(String str) {
        String regex = "^(([!-]?\\w+)|(\\([!-]?\\w+(\\|\\||\\\\/|\\+)[!-]?\\w+\\))(/\\\\|\\*|&&)?)+$";
        if (!Pattern.matches(regex, str)) {
            throw new IllegalArgumentException("Argument " + str + " doesn't match regex");
        }
        parseStr(str);
    }

    private void parseStr(String str) {
        ArrayList<String> pairs = new ArrayList<>();
        for (String subst : Arrays.stream(str.split("\\(")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new)) {
            for (String pair : Arrays.stream(subst.split("\\)")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new)) {
                String[] value = Arrays.stream(pair.split("(\\|\\||\\\\/|\\+)|(/\\\\|\\*|&&)")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new);
                if (value.length > 0) {
                    pairHashMap.put(
                            idDisjunction++,
                            new Pair(
                                    Pair.DISJUNCTION,
                                    value
                            )
                    );
                    addLiteral(pairHashMap.get(idDisjunction - 1).getLiteral1(), idDisjunction - 1);
                    addLiteral(pairHashMap.get(idDisjunction - 1).getLiteral2(), idDisjunction - 1);
                }
            }
        }
    }

    private void addLiteral(String literal, Integer idPair) {
        if (literal != null && !literal.isEmpty()) {
            HashSet<Integer> pairs = new HashSet<>();
            if (literals.containsKey(literal)) {
                pairs = literals.get(literal);
            }
            pairs.add(idPair);
            literals.put(
                    literal, pairs
            );
        }
    }

    public void addPair(Pair pair) {
        pairHashMap.put(idDisjunction, pair);
        addLiteral(pair.getLiteral1(), idDisjunction);
        addLiteral(pair.getLiteral2(), idDisjunction);
        idDisjunction++;
    }

    public HashMap<String, HashSet<Integer>> getLiterals() {
        return literals;
    }

    public HashMap<Integer, Pair> getPairHashMap() {
        return pairHashMap;
    }
}
