package solvers;

import entities.ConjunctiveNormalForm;
import entities.ConjunctiveNormalFormWithValue;
import entities.core.Literal;
import entities.core.PairWithValue;

import java.util.*;

public class DpllSolver implements ConjunctiveNormalFormSolver {
    private final ConjunctiveNormalFormWithValue simpleCnf;
    private final Map<String, HashSet<Integer>> cnfLiterals;
    private List<Literal> unsetLiterals;

    public DpllSolver(ConjunctiveNormalForm cnf) {
        simpleCnf = new ConjunctiveNormalFormWithValue(cnf);
        cnfLiterals = cnf.getLiterals();
        unsetLiterals = new ArrayList<>(simpleCnf.getLiterals().stream().toList());
    }

    @Override
    public boolean solve() {
        if (simpleCnf.getSatisfiable() == Literal.TRUE) {
            return true;
        }
        if (unsetLiterals.isEmpty()) {
            return false;
        }
        Literal l = findPureLiteral();
        if (l != null) return solve();
        l = findUnitClause();
        if (l != null) return solve();
        l = unsetLiterals.get(0);
        unsetLiterals.remove(l);
        l.value = Literal.TRUE;
        List<Literal> unsetLiteralsBefore = new ArrayList<>(unsetLiterals);
        boolean result = solve();
        if (!result) {
            l.value = Literal.FALSE;
            unsetLiterals = new ArrayList<>(unsetLiteralsBefore);
            for (Literal literal : unsetLiterals) {
                literal.value = Literal.UNINITIATED;
            }
            result = solve();
        }
        return result;
    }

    private Literal findPureLiteral() {
        Map<Integer, PairWithValue> pairs = simpleCnf.getPairHashMap();
        for (int i = 0; i < unsetLiterals.size(); ++i) {
            Literal literal = unsetLiterals.get(i);
            String l = unsetLiterals.get(i).name;
            Set<Integer> pairsL = cnfLiterals.get(l);
            int countL = 0, countInvL = 0;
            for (Integer idPair : pairsL) {
                PairWithValue pair = pairs.get(idPair);
                if ((pair.getLiteral1().value == Literal.UNINITIATED &&
                        (!pair.hasLiteral2() || pair.getLiteral2().value == Literal.UNINITIATED)) ||
                        ((pair.getLiteral1().equals(literal) && pair.getLiteral2().getValue(pair.isSign2()) != Literal.TRUE) ||
                                (pair.getLiteral2().equals(literal) && pair.getLiteral1().getValue(pair.isSign1()) != Literal.TRUE))) {
                    boolean sign;
                    if (pair.hasLiteral2()) {
                        sign = (pair.getLiteral1().equals(literal)) ? pair.isSign1() : pair.isSign2();
                    } else {
                        sign = pair.isSign1();
                    }
                    if (sign) {
                        countL++;
                    } else {
                        countInvL++;
                    }
                }
            }
            if (countL + countInvL != 0 && (countL == 0 || countInvL == 0)) {
                literal.value = (countL != 0) ? Literal.TRUE : Literal.FALSE;
                unsetLiterals.remove(i);
                return literal;
            }
        }
        return null;
    }

    private Literal findUnitClause() {
        for (Map.Entry<Integer, PairWithValue> set : simpleCnf.getPairHashMap().entrySet()) {
            PairWithValue pair = set.getValue();
            Literal literal = null;
            if (!pair.hasLiteral2() && pair.getLiteral1().value == Literal.UNINITIATED) {
                literal = pair.getLiteral1();
            } else if (pair.hasLiteral2() && pair.getSatisfiable() != Literal.TRUE) {
                Literal l1 = pair.getLiteral1(), l2 = pair.getLiteral2();
                literal = (l1.value == Literal.UNINITIATED && l2.value != Literal.UNINITIATED) ? l1 :
                        (l1.value != Literal.UNINITIATED && l2.value == Literal.UNINITIATED) ? l2 : null;
            }
            if (literal != null) {
                literal.value = (literal.equals(pair.getLiteral1())) ? pair.isSign1() ? Literal.TRUE : Literal.FALSE :
                        pair.isSign2() ? Literal.TRUE : Literal.FALSE;
                unsetLiterals.remove(literal);
                return literal;
            }
        }
        return null;
    }
}
