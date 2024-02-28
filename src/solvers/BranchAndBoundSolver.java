package solvers;

import entities.ConjunctiveNormalForm;
import entities.core.Literal;

import java.util.*;

public class BranchAndBoundSolver {
    public static boolean solve(ConjunctiveNormalForm cnf) {
        int result = getResult(0, cnf.getLiterals().stream().toList(), cnf);
        return result == Literal.TRUE;
    }

    private static int getResult(int id, List<Literal> literals,
                                 ConjunctiveNormalForm cnf) {
        if (cnf.getSatisfiable(true) == Literal.TRUE) {
            return Literal.TRUE;
        }
        if (id >= literals.size()) {
            return Literal.FALSE;
        }
        Literal literal = literals.get(id);
        literal.setValue(Literal.TRUE);
        int result = cnf.getSatisfiable(true);
        if (cnf.getSatisfiable(true) == Literal.UNINITIATED) {
            result = getResult(id + 1, literals, cnf);
        }
        if (result == Literal.FALSE) {
            literal.setValue(Literal.FALSE);
            result = getResult(id + 1, literals, cnf);
        }
        literal.setValue(Literal.UNINITIATED);
        return result;
    }
}
