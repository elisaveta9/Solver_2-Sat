package solvers;

import entities.ConjunctiveNormalForm;
import entities.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResolutionAndTransitiveSolution implements ConjunctiveNormalFormSolver {

    private final ConjunctiveNormalForm cnf;

    public ResolutionAndTransitiveSolution(ConjunctiveNormalForm cnf) {
        this.cnf = new ConjunctiveNormalForm(cnf);
    }

    public boolean solve() {
        if (!simplifyCnf()) {
            return false;
        }
        return checkCnf();
    }

    private boolean simplifyCnf() {
        HashMap<Integer, Pair> pairs = cnf.getPairHashMap();
        boolean flag;
        do {
            flag = false;
            for (Map.Entry<String, HashSet<Integer>> set : cnf.getLiterals().entrySet()) {
                HashSet<Integer> value = new HashSet<>(set.getValue());
                Pair prevPair = null;
                Integer idPrevPair = null;

                for (Integer idPair : value) {
                    Pair pair = pairs.get(idPair);
                    if (prevPair != null) {
                        if (prevPair.equals(pair)) {
                            cnf.removePair(idPair);
                            flag = true;
                        } else {
                            if (isContradictory(prevPair, pair)) {
                                return false;
                            }
                            Pair resultPair = reducePairs(prevPair, pair);
                            if (resultPair == null) {
                                resultPair = unionPairs(prevPair, pair);
                            }
                            if (resultPair != null) {
                                cnf.removePair(idPrevPair);
                                cnf.removePair(idPair);
                                cnf.addPair(resultPair);
                                flag = true;
                            }
                        }
                    }
                    prevPair = pair;
                    idPrevPair = idPair;
                }
            }
        } while (flag);
        return true;
    }

    private boolean checkCnf() {
        HashMap<Integer, Pair> pairs = cnf.getPairHashMap();
        Set<Integer> uncheckedPairs = new HashSet<>(pairs.keySet());
        for (Map.Entry<String, HashSet<Integer>> set : cnf.getLiterals().entrySet()) {
            HashSet<Integer> value = new HashSet<>(set.getValue());
            boolean isDoable = true;
            if (value.size() > 1) {
                Pair prevPair = null;
                for (Integer idPair : value) {
                    if (uncheckedPairs.contains(idPair)) {
                        Pair pair = pairs.get(idPair);
                        if (prevPair != null && uncheckedPairs.contains(idPair)) {
                            if (prevPair.getSign(set.getKey()) != pair.getSign(set.getKey())) {
                                isDoable = false;
                            }
                        }
                        prevPair = pair;
                    }
                }
            }
            if (isDoable) {
                for (Integer idPair : value) {
                    uncheckedPairs.remove(idPair);
                }
            }
        }
        return uncheckedPairs.isEmpty();
    }

    private boolean isContradictory(Pair pair1, Pair pair2) {
        if (!pair1.hasLiteral2() || !pair2.hasLiteral2()) {
            return false;
        }
        return (pair1.getLiteral1().equals(pair2.getLiteral2()) &&
                pair2.getLiteral1().equals(pair1.getLiteral2()) &&
                pair1.isSign1() != pair2.isSign2() &&
                pair2.isSign1() != pair1.isSign2()) || (
                pair1.getLiteral1().equals(pair2.getLiteral1()) &&
                pair1.getLiteral2().equals(pair2.getLiteral2()) &&
                pair1.isSign1() != pair2.isSign1() &&
                pair1.isSign2() != pair2.isSign2()
        );
    }

    private Pair unionPairs(Pair pair1, Pair pair2) {
        String removedLiteral = canUnion(pair1, pair2);
        if (removedLiteral.isEmpty()) {
            return null;
        }

        String literal1, literal2;
        boolean sign1, sign2;

        if (pair1.getLiteral1().equals(removedLiteral)) {
            literal1 = pair1.getLiteral2();
            sign1 = pair1.isSign2();
        } else {
            literal1 = pair1.getLiteral1();
            sign1 = pair1.isSign1();
        }

        if (pair2.getLiteral1().equals(removedLiteral)) {
            literal2 = pair2.getLiteral2();
            sign2 = pair2.isSign2();
        } else {
            literal2 = pair2.getLiteral1();
            sign2 = pair2.isSign1();
        }

        return new Pair(
                Pair.DISJUNCTION,
                literal1,
                sign1,
                literal2,
                sign2
        );
    }

    private Pair reducePairs(Pair pair1, Pair pair2) {
        String removedLiteral = canReduce(pair1, pair2);
        if (removedLiteral.isEmpty()) {
            return null;
        }
        String literal1;
        boolean sign1;

        if (pair1.getLiteral1().equals(removedLiteral)) {
            literal1 = pair1.getLiteral2();
            sign1 = pair1.isSign2();
        } else {
            literal1 = pair1.getLiteral1();
            sign1 = pair1.isSign1();
        }

        return new Pair(
                Pair.DISJUNCTION,
                literal1,
                sign1
        );
    }

    private String canReduce(Pair pair1, Pair pair2) {
        if (!pair1.hasLiteral2() || !pair2.hasLiteral2()) {
            return "";
        }
        if (pair1.getLiteral1().equals(pair2.getLiteral1()) && pair1.getLiteral2().equals(pair2.getLiteral2())) {
            if (pair1.isSign1() == pair2.isSign1() && pair1.isSign2() != pair2.isSign2()) {
                return pair1.getLiteral2();
            } else if (pair1.isSign1() != pair2.isSign1() && pair1.isSign2() == pair2.isSign2()) {
                return pair1.getLiteral1();
            }
        } else if (pair1.getLiteral1().equals(pair2.getLiteral2()) && pair1.getLiteral2().equals(pair2.getLiteral1())) {
            if (pair1.isSign1() == pair2.isSign2() && pair1.isSign2() != pair2.isSign1()) {
                return pair1.getLiteral2();
            } else if (pair1.isSign1() != pair2.isSign2() && pair1.isSign2() == pair2.isSign1()) {
                return pair1.getLiteral1();
            }
        }
        return "";
    }

    private String canUnion(Pair pair1, Pair pair2) {
        String defaultAnswer = "";
        if (!pair1.hasLiteral2() || !pair2.hasLiteral2()) {
            return defaultAnswer;
        }
        if (((pair1.getLiteral1().equals(pair2.getLiteral1()) && pair1.getLiteral2().equals(pair2.getLiteral2())) ||
                (pair1.getLiteral1().equals(pair2.getLiteral2()) && pair1.getLiteral2().equals(pair2.getLiteral1())))) {
            return defaultAnswer;
        }
        if ((pair1.getLiteral1().equals(pair2.getLiteral1()) && pair1.isSign1() != pair2.isSign1()) ||
                (pair1.getLiteral1().equals(pair2.getLiteral2()) && pair1.isSign1() != pair2.isSign2())) {
            return pair1.getLiteral1();
        } else if ((pair1.getLiteral2().equals(pair2.getLiteral1()) && pair1.isSign2() != pair2.isSign1()) ||
                (pair1.getLiteral2().equals(pair2.getLiteral2()) && pair1.isSign2() != pair2.isSign2())) {
            return pair1.getLiteral2();
        }
        return defaultAnswer;
    }

    @Override
    public String toString() {
        return cnf.toString();
    }
}
