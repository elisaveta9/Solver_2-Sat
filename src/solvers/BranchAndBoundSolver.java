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
    private List<Literal> solution;

    public BranchAndBoundSolver(ConjunctiveNormalFormWithValue cnf) {
        this.cnf = new ConjunctiveNormalFormWithValue(cnf);
    }

    public BranchAndBoundSolver(ConjunctiveNormalForm cnf) {
        this.cnf = new ConjunctiveNormalFormWithValue(cnf);
        literals = this.cnf.getLiterals();
        solution = new ArrayList<>();
    }

    @Override
    public boolean solve() {
        int result = getResult(0);
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
}
