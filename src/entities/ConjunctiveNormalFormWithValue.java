package entities;

import entities.core.IntHashSet;
import entities.core.Literal;
import entities.core.Pair;
import entities.core.PairWithValue;

import java.util.*;

public class ConjunctiveNormalFormWithValue {
    private Map<Literal, IntHashSet> literals = new TreeMap<>();
    private Map<Integer, PairWithValue> pairHashMap = new TreeMap<>();
    private int idPair = 0;

    public ConjunctiveNormalFormWithValue(ConjunctiveNormalForm cnf) {
        HashMap<Integer, Pair> pairs = cnf.getPairHashMap();
        for (Map.Entry<String, HashSet<Integer>> literalInfo : cnf.getLiterals().entrySet()) {
            String name = literalInfo.getKey();
            Literal literal = new Literal(name, true),
                    literalInv = new Literal(name, false);
            IntHashSet literalSet = new IntHashSet(),
                    literalInvSet = new IntHashSet();
            for (int id : literalInfo.getValue()) {
                idPair = Math.max(idPair, id);
                Pair pair = pairs.get(id);
                if ((pair.getLiteral1().equals(name) && pair.isSign1()) ||
                        (pair.getLiteral2().equals(name) && pair.isSign2())) {
                    addPair(id, literal, pair.getType());
                    literalSet.add(id);
                } else if ((pair.getLiteral1().equals(name) && !pair.isSign1()) ||
                        (pair.getLiteral2().equals(name) && !pair.isSign2())) {
                    addPair(id, literalInv, pair.getType());
                    literalInvSet.add(id);
                }
            }
            if (!literalSet.isEmpty()) literals.put(literal, literalSet);
            if (!literalInvSet.isEmpty()) literals.put(literalInv, literalInvSet);
        }
        idPair++;
    }

    public ConjunctiveNormalFormWithValue(ConjunctiveNormalFormWithValue cnf) {
        literals = new LinkedHashMap<>(cnf.literals);
        pairHashMap = new HashMap<>(cnf.pairHashMap);
        idPair = cnf.idPair;
    }

    private void addPair(Integer id, Literal literal, int type) {
        if (pairHashMap.containsKey(id)) {
            PairWithValue pair = pairHashMap.get(id);
            pair.addLiteral2(literal);
        } else {
            PairWithValue pair = new PairWithValue(literal, null, type);
            pairHashMap.put(id, pair);
        }
    }

    /*private void removePairLiteral(Literal literal, Integer id) {
        if (literal != null && !literal.name.isEmpty()) {
            HashSet<Integer> pairs = new HashSet<>();
            if (literals.containsKey(literal)) {
                pairs = literals.get(literal);
            }
            pairs.remove(id);
            literals.put(
                    literal, pairs
            );
        }
    }*/

    /*public boolean removePair(Integer id) {
        if (!pairHashMap.containsKey(id)) {
            return false;
        }
        PairWithValue pair = pairHashMap.get(id);
        pairHashMap.remove(id);
        removePairLiteral(pair.getLiteral1(), id);
        removePairLiteral(pair.getLiteral2(), id);
        return true;
    }*/

    public int getSatisfiable() {
        for (Map.Entry<Integer, PairWithValue> pair : pairHashMap.entrySet()) {
            if (pair.getValue().getSatisfiable() == Literal.UNINITIATED)
                return Literal.UNINITIATED;
            else if (pair.getValue().getSatisfiable() == Literal.FALSE)
                return Literal.FALSE;
        }
        return Literal.TRUE;
    }

    public Map<Literal, IntHashSet> getLiterals() {
        return literals;
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
