package solvers;

import entities.ConjunctiveNormalForm;
import entities.core.Clause;
import entities.core.Literal;
import entities.core.SingleLiteral;

import java.util.*;

public class DpllSolver {
    public static boolean solve(ConjunctiveNormalForm cnf) {
        List<Literal> unsetLiterals = cnf.getUnsetLiterals();
        return solve(cnf, unsetLiterals);
    }

    private static boolean solve(ConjunctiveNormalForm cnf, List<Literal> unsetLiterals) {
        if (cnf.getSatisfiable(true) == Literal.TRUE) {
            return true;
        }
        if (unsetLiterals.isEmpty()) {
            return false;
        }
        Literal literal = findPureLiteral(cnf, unsetLiterals);
        if (literal != null)
            return solve(cnf, cnf.getUnsetLiterals());
        literal = findUnitClause(cnf, unsetLiterals);
        if (literal != null)
            return solve(cnf, cnf.getUnsetLiterals());
        literal = unsetLiterals.get(0);
        literal.setValue(Literal.TRUE);
        boolean result = solve(cnf, cnf.getUnsetLiterals());
        if (!result) {
            literal.setValue(Literal.FALSE);
            result = solve(cnf, cnf.getUnsetLiterals());
        }
        literal.setValue(Literal.UNINITIATED);
        return result;
    }

    private static Literal findPureLiteral(ConjunctiveNormalForm cnf, List<Literal> unsetLiterals) {
        for (Literal literal : unsetLiterals) {
            Integer sign = null;
            List<Integer> idClauses = cnf.getIdClauses(literal).stream().toList();
            boolean fl = true;
            for (int i = 0; fl && i < idClauses.size(); ++i) {
                Clause clause = cnf.getClauseMap().get(idClauses.get(i));
                if (clause instanceof SingleLiteral ||
                        (clause.getSatisfiable(true) != Literal.TRUE)) {
                    if (sign == null) sign = clause.getSign(literal);
                    else {
                        HashSet<Integer> signs = new HashSet<>(clause.getSignsLiteral(literal));
                        HashSet<Literal> literals = new HashSet<>(clause.getLiterals());
                        fl = sign.equals(clause.getSign(literal)) ||
                                (literals.size() == 1 && signs.size() == 2);
                    }
                }
            }
            if (fl && sign != null) {
                literal.setValue(sign);
                return literal;
            }
        }
        return null;
    }

    private static Literal findUnitClause(ConjunctiveNormalForm cnf, List<Literal> unsetLiterals) {
        for (Literal literal : unsetLiterals) {
            List<Integer> idClauses = cnf.getIdClauses(literal).stream().toList();
            for (int i = 0; i < idClauses.size(); ++i) {
                Clause clause = cnf.getClauseMap().get(idClauses.get(i));
                List<Literal> literals = clause.getUnsetValueLiterals();
                if (literals.size() == 1 && (clause instanceof SingleLiteral ||
                        clause.getSatisfiable(true) != Literal.TRUE)) {
                    literal.setValue(clause.getSign(literal));
                    return literal;
                }
            }
        }
        return null;
    }
}