import entities.ConjunctiveNormalForm;
import entities.core.Clause;
import entities.core.Disjunction;
import entities.core.Literal;
import entities.core.SingleLiteral;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratorCnf {
    private static final Random random = new Random();

    public static ConjunctiveNormalForm generate(int literals, int clauses) {
        if (literals < 0 || clauses < 0 || literals > clauses * 2) {
            throw new IllegalArgumentException();
        }
        ConjunctiveNormalForm cnf = new ConjunctiveNormalForm();

        List<String> literalsList = new ArrayList<>();
        for (int i = 0; i < literals; ++i) {
            literalsList.add("X" + i);
        }

        for (int i = 0; i < clauses; ++i) {
            Clause clause;
            if (random.nextInt() % 5 == 1) {
                clause = new SingleLiteral(
                        new Literal(literalsList.get(Math.abs(random.nextInt() % literals)), Literal.UNINITIATED),
                        random.nextBoolean() ? Literal.TRUE : Literal.FALSE
                );
            } else {
                clause = new Disjunction();
                clause.addLiteral(
                        new Literal(literalsList.get(Math.abs(random.nextInt() % literals)), Literal.UNINITIATED),
                        random.nextBoolean() ? Literal.TRUE : Literal.FALSE
                );
                clause.addLiteral(
                        new Literal(literalsList.get(Math.abs(random.nextInt() % literals)), Literal.UNINITIATED),
                        random.nextBoolean() ? Literal.TRUE : Literal.FALSE
                );
            }
            cnf.addClause(clause);
        }

        return cnf;
    }
}
