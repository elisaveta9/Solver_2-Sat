package entities;

import entities.core.Literal;
import entities.core.Pair;
import entities.core.PairWithValue;

import java.util.*;

public class ConjunctiveNormalFormWithValue {
    private Map<Literal, HashSet<Integer>> literals = new TreeMap<>();
    private Map<Integer, PairWithValue> pairHashMap = new TreeMap<>();
    private int idPair = 0;

    public ConjunctiveNormalFormWithValue(ConjunctiveNormalForm cnf) {
        HashMap<Integer, Pair> pairs = cnf.getPairHashMap();
        for (Map.Entry<String, HashSet<Integer>> literalInfo : cnf.getLiterals().entrySet()) {
            String name = literalInfo.getKey();
            Literal literal = new Literal(name);
            HashSet<Integer> idPairs = new HashSet<>();
            for (int id : literalInfo.getValue()) {
                idPair = Math.max(idPair, id);
                Pair pair = pairs.get(id);
                boolean sign = (pair.getLiteral1().equals(literal.name)) ? pair.isSign1() : pair.isSign2();
                addPair(id, sign, literal);
                idPairs.add(id);
            }
            literals.put(literal, idPairs);
        }
        idPair++;
    }

    public ConjunctiveNormalFormWithValue(ConjunctiveNormalFormWithValue cnf) {
        literals = new TreeMap<>(cnf.literals);
        pairHashMap = new TreeMap<>(cnf.pairHashMap);
        idPair = cnf.idPair;
    }

    private void addPair(Integer id, boolean sign, Literal literal) {
        if (pairHashMap.containsKey(id)) {
            PairWithValue pair = pairHashMap.get(id);
            pair.addLiteral2(literal, sign);
        } else {
            PairWithValue pair = new PairWithValue(1, literal, sign);
            pairHashMap.put(id, pair);
        }
    }

    public int getSatisfiable() {
        for (Map.Entry<Integer, PairWithValue> pair : pairHashMap.entrySet()) {
            int value = pair.getValue().getSatisfiable();
            if (value == Literal.UNINITIATED)
                return Literal.UNINITIATED;
            if (value == Literal.FALSE)
                return Literal.FALSE;
        }
        return Literal.TRUE;
    }

    public Set<Literal> getLiterals() {
        return literals.keySet();
    }

    public Map<Integer, PairWithValue> getPairHashMap() {
        return pairHashMap;
    }

    @Override
    public String toString() {
        boolean hasPrev = false;
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, PairWithValue> set : pairHashMap.entrySet()) {
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
        ConjunctiveNormalFormWithValue that = (ConjunctiveNormalFormWithValue) o;
        return Objects.equals(literals, that.literals) && Objects.equals(pairHashMap, that.pairHashMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literals, pairHashMap);
    }
}
