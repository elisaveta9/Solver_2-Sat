package solvers;

import entities.ConjunctiveNormalForm;
import entities.ConjunctiveNormalFormWithValue;
import entities.core.Literal;

import java.util.List;
import java.util.Random;

public class MonteCarloMethodSolver implements ConjunctiveNormalFormSolver {


    private final ConjunctiveNormalFormWithValue cnf;
    private List<Literal> literals;
    private final Random random = new Random();

    public MonteCarloMethodSolver(ConjunctiveNormalForm cnf) {
        this.cnf = new ConjunctiveNormalFormWithValue(cnf);
        literals = this.cnf.getLiterals();
    }

    @Override
    public boolean solve() {
        int s = 0, mS = (int) (Math.pow(2, literals.size()) * 0.95);
        for (; s < mS; s++) {
            if (nextValue() == Literal.TRUE) {
                return true;
            }
        }
        return false;
    }

    public int nextValue() {
        for (Literal l : literals) {
            l.value = random.nextBoolean() ? Literal.TRUE : Literal.FALSE;
        }
        return cnf.getSatisfiable();
    }
}
