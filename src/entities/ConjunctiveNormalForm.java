package entities;

import entities.core.Pair;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

public class ConjunctiveNormalForm {

    private HashMap<String, HashSet<Integer>> literals = new HashMap<>();
    private HashMap<Integer, Pair> pairHashMap = new HashMap<>();
    private int idPair = 0;

    public ConjunctiveNormalForm() { }

    public ConjunctiveNormalForm(String str) {
        str = str.replace("/\\", "*");
        str = str.replace("&&", "*");
        str = str.replace("\\/", "+");
        str = str.replace("||", "+");
        String regex = "^(((!?[a-zA-Z\\d]+)|(\\(!?[a-zA-Z\\d]+\\+!?[a-zA-Z\\d]+\\)))+\\*?)+$";
        if (!Pattern.matches(regex, str)) {
            throw new IllegalArgumentException("Argument " + str + " doesn't match regex");
        }
        parseStr(str);
    }

    public ConjunctiveNormalForm(ConjunctiveNormalForm cnf) {
        literals = new HashMap<>(cnf.literals);
        pairHashMap = new HashMap<>(cnf.pairHashMap);
        idPair = cnf.idPair;
    }

    private void parseStr(String str) {

        for (String subst : Arrays.stream(str.split("\\*")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new)) {
            for (String pair : Arrays.stream(subst.split("(\\()|(\\))")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new)) {
                String[] value = Arrays.stream(pair.split("\\+")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new);
                if (value.length > 0) {
                    addPair(new Pair(
                            Pair.DISJUNCTION,
                            value
                    ));
                }
            }
        }
    }

    private void addLiteral(String literal, Integer id) {
        if (literal != null && !literal.isEmpty()) {
            HashSet<Integer> pairs = new HashSet<>();
            if (literals.containsKey(literal)) {
                pairs = literals.get(literal);
            }
            pairs.add(id);
            literals.put(
                    literal, pairs
            );
        }
    }

    private void removePairLiteral(String literal, Integer id) {
        if (literal != null && !literal.isEmpty()) {
            HashSet<Integer> pairs = new HashSet<>();
            if (literals.containsKey(literal)) {
                pairs = literals.get(literal);
            }
            pairs.remove(id);
            literals.put(
                    literal, pairs
            );
        }
    }

    public int addPair(Pair pair) {
        if (pair.getLiteral1() != null && pair.getLiteral2() != null) {
            pairHashMap.put(idPair, pair);
            addLiteral(pair.getLiteral1(), idPair);
            addLiteral(pair.getLiteral2(), idPair);
            idPair++;
            return idPair - 1;
        }
        return -1;
    }

    public void removePair(Integer id) {
        if (!pairHashMap.containsKey(id)) {
            return;
        }
        Pair pair = pairHashMap.get(id);
        pairHashMap.remove(id);
        removePairLiteral(pair.getLiteral1(), id);
        removePairLiteral(pair.getLiteral2(), id);
    }

    public HashMap<String, HashSet<Integer>> getLiterals() {
        return literals;
    }

    public HashMap<Integer, Pair> getPairHashMap() {
        return pairHashMap;
    }

    @Override
    public String toString() {
        boolean hasPrev = false;
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, Pair> set : pairHashMap.entrySet()) {
            if (hasPrev) {
                result.append("*");
            } else {
                hasPrev = true;
            }
            result.append(set.getValue());
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConjunctiveNormalForm that = (ConjunctiveNormalForm) o;
        return Objects.equals(literals, that.literals) && Objects.equals(pairHashMap, that.pairHashMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literals, pairHashMap);
    }
}
