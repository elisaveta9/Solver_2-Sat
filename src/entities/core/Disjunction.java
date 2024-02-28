package entities.core;

import java.util.Arrays;
import java.util.List;

public class Disjunction extends Clause {
    private static final String DISJUNCTION_PATTERN = "[-!]?[a-zа-яA-ZА-Я\\d]+(((\\\\/)|\\+)[-!]?[a-zа-яA-ZА-Я\\d]+)+";

    public Disjunction() { super(); }

    public Disjunction(Clause clause) { super(clause);}

    public Disjunction(Literal... src) {
        super(src);
    }

    public Disjunction(List<Literal> literals) {
        super(literals);
    }

    public Disjunction(List<Literal> literals, List<Integer> signs) {
        super(literals, signs);
    }

    public Disjunction(String[] src) {
        super(src);
    }

    public static Disjunction parse(String src) {
        if (!src.matches(DISJUNCTION_PATTERN))
            throw new IllegalArgumentException("String '" + src + "' doesn't matches disjunction pattern");
        String[] literals = Arrays.stream(src.split("(\\\\/)|\\+")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new);
        return new Disjunction(literals);
    }

    @Override
    public boolean isSatisfiable(boolean useSigns) {
        return useSigns ? isSatisfiableUseSigns() : isSatisfiable();
    }

    private boolean isSatisfiable() {
        for (Literal l : getLiterals()) {
            if (l.isSatisfiable() != Literal.FALSE)
                return true;
        }
        return false;
    }

    private boolean isSatisfiableUseSigns() {
        if (getSigns().size() != getLiterals().size())
            throw new RuntimeException();
        List<Literal> literals = getLiterals();
        List<Integer> signs = getSigns();
        int i = 0;
        for (Literal l : getLiterals()) {
            if (l.isSatisfiable(signs.get(i++), l.getValue()) != Literal.FALSE)
                return true;
        }
        return false;
    }

    @Override
    public int getSatisfiable(boolean useSigns) {
        return useSigns ? getSatisfiableUseSigns() : getSatisfiable();
    }

    private int getSatisfiable() {
        int sat = 0;
        for (Literal l : getLiterals()) {
            int lSat = l.isSatisfiable();
            if (lSat == Literal.TRUE)
                return lSat;
            sat += lSat;
        }
        return sat == 2 * getLiterals().size() ? Literal.FALSE : Literal.UNINITIATED;
    }

    private int getSatisfiableUseSigns() {
        if (getSigns().size() != getLiterals().size())
            throw new RuntimeException();
        int sat = 0, i = 0;
        List<Literal> literals = getLiterals();
        List<Integer> signs = getSigns();
        for (Literal l : literals) {
            int lSat = l.isSatisfiable(signs.get(i++), l.getValue());
            if (lSat == Literal.TRUE)
                return lSat;
            sat += lSat;
        }
        return sat == 2 * getLiterals().size() ? Literal.FALSE : Literal.UNINITIATED;
    }

    @Override
    public String toString() {
        if (getLiterals().isEmpty())
            return "";
        List<Literal> literals = getLiterals();
        StringBuilder result = new StringBuilder();
        if (literals.size() != 1) {
            result = new StringBuilder("(");
        }
        if (getSigns().size() != literals.size()) {
            result.append(literals.get(0).toString());
            for (int i = 1; i < literals.size(); ++i)
                result.append("+").append(literals.get(i).toString());
        } else {
            List<Integer> signs = getSigns();
            result.append(literals.get(0).toString(signs.get(0)));
            for (int i = 1; i < literals.size(); ++i)
                result.append("+").append(literals.get(i).toString(signs.get(i)));
        }
        return literals.size() != 1 ? result.append(")").toString() : result.toString();
    }
}
