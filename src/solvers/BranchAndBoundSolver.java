package solvers;

import entities.ConjunctiveNormalForm;
import entities.ConjunctiveNormalFormWithValue;
import entities.core.IntHashSet;
import entities.core.Literal;
import entities.core.PairWithValue;

import java.util.*;
import java.util.stream.Collectors;

public class BranchAndBoundSolver implements ConjunctiveNormalFormSolver, HasSolution{

    private final ConjunctiveNormalFormWithValue cnf;
    private List<Literal> literals;
    private List<Literal> solution = new ArrayList<>();

    public BranchAndBoundSolver(ConjunctiveNormalFormWithValue cnf) {
        this.cnf = new ConjunctiveNormalFormWithValue(cnf);
    }

    public BranchAndBoundSolver(ConjunctiveNormalForm cnf) {
        this.cnf = new ConjunctiveNormalFormWithValue(cnf);
        this.literals = this.cnf.getLiterals();
    }

    @Override
    public boolean solve() {
        int result = getResult(0);
        /*Map<Literal, IntHashSet> map = cnf.getLiterals().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));*/
        /*Map<Literal, IntHashSet> map = cnf.getLiterals();
        for (Map.Entry<Literal, IntHashSet> pair : map.entrySet()) {
            Literal literal = pair.getKey();
            //int value = getValue(literal, pair.getValue(),
            //        literalInv, map.containsKey(literalInv) ? map.get(literalInv) : new IntHashSet());
            literal.setValue(Literal.TRUE);
            if (cnf.getSatisfiable() == Literal.FALSE)
                literal.setValue(Literal.UNINITIATED);
        }
        int result = cnf.getSatisfiable();*/
        return result == Literal.TRUE;
    }

    private int getResult(int id) {
        if (id >= literals.size()) {
            return Literal.FALSE;
        }
        Literal literal = literals.get(id);
        literal.setValue(Literal.TRUE);
        int result = cnf.getSatisfiable();
        if (cnf.getSatisfiable() == Literal.UNINITIATED) {
            result = getResult(id + 1);
        }
        if (result == Literal.FALSE) {
            literal.setValue(Literal.FALSE);
            result = getResult(id + 1);
        }
        if (result == Literal.TRUE && solution.isEmpty()) {
            for (Literal l : literals) {
                solution.add(new Literal(l));
            }
        }
        literal.setValue(Literal.UNINITIATED);
        return result;
    }

    public List<Literal> getSolution() {
        return solution;
    }

    @Override
    public String toString() {
        return cnf.toString();
    }
/*private int getValue(Literal literal1, IntHashSet set1, Literal literal2, IntHashSet set2) {
        Map<Integer, PairWithValue> map = cnf.getPairHashMap();
        int c1 = 0, c2 = 0;
        for (Integer id : set1) {
            if (map.get(id).getSatisfiable() == Literal.FALSE)
                c1++;
        }
        for (Integer id : set2) {
            if (map.get(id).getSatisfiable() == Literal.FALSE)
                c2++;
        }
        if (c1 > c2) {
            return literal1.sign ? Literal.TRUE : Literal.FALSE;
        }
        return literal2.sign ? Literal.TRUE : Literal.FALSE;
    }*/
}
