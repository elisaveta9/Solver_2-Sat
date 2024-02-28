package solvers;

import entities.ConjunctiveNormalForm;
import entities.core.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResolutionAndTransitiveSolution implements ConjunctiveNormalFormSolver {

    private final ConjunctiveNormalForm cnf;

    public ResolutionAndTransitiveSolution(ConjunctiveNormalForm cnf) {
        this.cnf = new ConjunctiveNormalForm(cnf);
    }

    @Override
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
                if (value.size() > 1) {
                    for (Integer idPair : value) {
                        Pair pair = pairs.get(idPair);
                        Response smp = null;
                        if (prevPair != null) {
                            if (prevPair.equals(pair)) {
                                cnf.removePair(idPair);
                                flag = true;
                            } else {
                                if (isContradictory(prevPair, pair)) {
                                    return false;
                                }
                                smp = simplifyPair(pair, idPair, prevPair, idPrevPair);
                            }
                        }
                        if (smp == null) {
                            prevPair = pair;
                            idPrevPair = idPair;
                        } else if (smp.removedPairs == 2) {
                            prevPair = smp.resultPair;
                            idPrevPair = smp.idNewPair;
                            flag = true;
                        } else {
                            flag = true;
                            if (!pair.hasLiteral2()) {
                                prevPair = pair;
                                idPrevPair = idPair;
                            }
                        }
                    }
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
                pair1.getLiteral1().equals(pair1.getLiteral2()) &&
                pair1.isSign1() != pair2.isSign2() &&
                pair2.isSign1() != pair1.isSign2());
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

    private Response reducePair(Pair pair1, Pair pair2) {
        if ((pair1.hasLiteral2() && pair2.hasLiteral2()) ||
                (!pair1.hasLiteral2() && !pair2.hasLiteral2())) {
            return null;
        }
        Pair a = (pair1.hasLiteral2()) ? pair1 : pair2,
                b = (pair1.hasLiteral2()) ? pair2 : pair1;
        int id = (a.getLiteral1().equals(b.getLiteral1())) ? 1 :
                (a.getLiteral2().equals(b.getLiteral1())) ? 2 : 0;
        switch (id) {
            case 0 -> {
                return null;
            }
            case 1 -> {
                Pair p = (a.isSign1() != b.isSign1()) ? new Pair(a.getType(), a.getLiteral2(), a.isSign2()) :
                        null;
                return new Response(1, p);
            }
            case 2 -> {
                Pair p = (a.isSign2() != b.isSign1()) ? new Pair(a.getType(), a.getLiteral1(), a.isSign1()) :
                        null;
                return new Response(1, p);
            }
        }
        return null;
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

    private Response simplifyPair(Pair pair1, int idPair, Pair pair2, int idPrevPair) {
        Response result = reducePair(pair1, pair2);
        if (result != null) {
            int id = (pair1.hasLiteral2()) ? idPair : idPrevPair;
            cnf.removePair(id);
            if (result.resultPair != null) {
                int idNewPair = cnf.addPair(result.resultPair);
            }
            return result;
        }
        Pair resultPair = reducePairs(pair1, pair2);
        if (resultPair == null) {
            resultPair = unionPairs(pair1, pair2);
        }
        if (resultPair != null) {
            cnf.removePair(idPrevPair);
            cnf.removePair(idPair);
            int idNewPair = cnf.addPair(resultPair);
            return new Response(2, idNewPair, resultPair);
        }
        return null;
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

    private static class Response {
        public int removedPairs = 0;
        public int idNewPair = 0;
        public Pair resultPair;

        public Response(int removedPairs, int idNewPair, Pair resultPair) {
            this.removedPairs = removedPairs;
            this.idNewPair = idNewPair;
            this.resultPair = resultPair;
        }

        public Response(int removedPairs, Pair resultPair) {
            this.removedPairs = removedPairs;
            this.resultPair = resultPair;
        }
    }
}
