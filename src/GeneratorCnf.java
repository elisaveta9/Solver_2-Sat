import entities.ConjunctiveNormalForm;
import entities.core.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GeneratorCnf {
    private static final Random random = new Random();

    public static ConjunctiveNormalForm generate(int literals, int clauses) {
        if (literals < 0 || clauses < 0 || literals > clauses * 2) {
            throw new IllegalArgumentException();
        }
        ConjunctiveNormalForm cnf = new ConjunctiveNormalForm();
        List<String> listLiterals = new ArrayList<>();
        for (int i = 0; i < literals; ++i) {
            listLiterals.add("X" + i);
        }
        for (int i = 0; i < clauses; ++i) {
            Pair pair = new Pair(Pair.DISJUNCTION);
            pair.addLiteral(
                    listLiterals.get(Math.abs(random.nextInt() % literals)),
                    random.nextBoolean()
            );
            if (random.nextBoolean()) {
                pair.addLiteral(
                        listLiterals.get(Math.abs(random.nextInt() % literals)),
                        random.nextBoolean()
                );
            }
            cnf.addPair(pair);
        }
        return cnf;
    }
}
