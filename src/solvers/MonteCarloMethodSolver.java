package solvers;

import entities.ConjunctiveNormalForm;
import entities.core.Literal;

import java.util.List;
import java.util.Random;

public class MonteCarloMethodSolver {
    private static final Random random = new Random();
    public static boolean solve(ConjunctiveNormalForm cnf) {
        int s = 0, mS = (int) (Math.pow(2, cnf.getLiterals().size()) * 0.995);
        for (; s < mS; s++) {
            if (nextValue(cnf) == Literal.TRUE) {
                return true;
            }
        }
        return false;
    }

    private static int nextValue(ConjunctiveNormalForm cnf) {
        for (Literal l : cnf.getLiterals()) {
            if (random.nextBoolean())
                l.setValue(Literal.TRUE);
            else l.setValue(Literal.FALSE);
        }
        return cnf.getSatisfiable(true);
    }
}
