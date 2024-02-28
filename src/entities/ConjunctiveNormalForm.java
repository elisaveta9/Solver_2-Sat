package entities;

import entities.core.Clause;
import entities.core.Disjunction;
import entities.core.Literal;
import entities.core.SingleLiteral;

import java.util.*;
import java.util.regex.Pattern;

public class ConjunctiveNormalForm {
    private static final String CONJUNCTIVE_NORMAL_FORM_PATTERN =
            "((([-!]?[a-zа-яA-ZА-Я\\d]+)|(\\([-!]?[a-zа-яA-ZА-Я\\d]+(\\+|\\\\/)[-!]?[a-zа-яA-ZА-Я\\d]+\\)))+(\\*|/\\\\)?)+";

    private final Map<Literal, HashSet<Integer>> literalsMap = new HashMap<>();
    private final Map<Integer, Clause> clauseMap  = new HashMap<>();
    private int idClause = 0;

    public ConjunctiveNormalForm() { }

    public ConjunctiveNormalForm(ConjunctiveNormalForm cnf) {
        for (Literal literal : cnf.getLiterals()) {
            literalsMap.put(new Literal(literal, false), new HashSet<>());
        }
        for (Clause clause : cnf.clauseMap.values()) {
            if (clause instanceof Disjunction)
                addClause(new Disjunction(clause));
            else addClause(new SingleLiteral(clause));
        }
    }

    public static ConjunctiveNormalForm parseCnf(String src) {
        if (!src.matches(CONJUNCTIVE_NORMAL_FORM_PATTERN))
            throw new IllegalArgumentException("String '" + src + "' doesn't matches cnf pattern");
        ConjunctiveNormalForm cnf = new ConjunctiveNormalForm();
        for (String clStr : Arrays.stream(src.split("(\\()|(\\))|(\\*)|(/\\\\)")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new)) {
            Clause clause;
            try {
                clause = SingleLiteral.parse(clStr);
            } catch (Exception e) {
                clause = Disjunction.parse(clStr);
            }
            cnf.addClause(clause);
        }
        return cnf;
    }

    public static ConjunctiveNormalForm parseCnf(String src, boolean saveSignsAsList) {
        if (!saveSignsAsList)
            return parseCnf(src);
        if (!src.matches(CONJUNCTIVE_NORMAL_FORM_PATTERN))
            throw new IllegalArgumentException("String '" + src + "' doesn't matches cnf pattern");
        ConjunctiveNormalForm cnf = new ConjunctiveNormalForm();
        for (String clStr : Arrays.stream(src.split("(\\()|(\\))|(\\*)|(/\\\\)")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new)) {
            Clause clause;
            try {
                clause = SingleLiteral.parse(clStr);
            } catch (Exception e) {
                clause = Disjunction.parse(clStr);
            }
            for (Literal literal : clause.getLiterals())
                literal.setSign(Literal.UNINITIATED);
            cnf.addClause(clause);
        }
        return cnf;
    }

    private Literal addLiteral(Literal literal, Integer idClause) {
        Set<Literal> literals = getLiterals();
        if (literals.contains(literal)) {
            literalsMap.get(literal).add(idClause);
            for (Literal l : literals)
                if (l.equals(literal))
                    return l;
        } else {
            HashSet<Integer> value = new HashSet<>();
            value.add(idClause);
            literalsMap.put(literal, value);
            return literal;
        }
        return null;
    }

    public void removeLiteralFromClause(Literal literal, Clause clause, Integer index) {
        clause.removeLiteral(literal);
        literalsMap.get(literal).remove(index);
        if (clause.getLiterals().isEmpty())
            removeClause(index);
        else if (clause.getLiterals().size() == 1) {
            clauseMap.put(index, new SingleLiteral(clause));
        }
    }

    public void addClause(Clause clause) {
        if (!(clause instanceof SingleLiteral || clause instanceof Disjunction)) {
            throw new IllegalArgumentException("Clause of the '" + clause.getClass() + "' type cannot be added to cnf.");
        }
        if (clause instanceof Disjunction) {
            Set<Literal> literalSet = new HashSet<>(clause.getLiterals());
            Set<Integer> signSet = new HashSet<>(clause.getSigns());
            if (literalSet.size() == signSet.size() && literalSet.size() == 1) {
                clause = new SingleLiteral(
                        clause.getLiteral(0),
                        clause.getSigns().get(0)
                );
            }
        }
        List<Literal> components = new ArrayList<>();
        for (Literal literal : clause.getLiterals()) {
            components.add(addLiteral(literal, idClause));
        }
        clause.setLiterals(components);
        clauseMap.put(idClause++, clause);
    }

    public void removeClause(Integer index) {
        Clause clause = clauseMap.get(index);
        for (Literal literal : clause.getLiterals())
            literalsMap.get(literal).remove(index);
        clauseMap.remove(index);
    }

    public Set<Literal> getLiterals() {
        return literalsMap.keySet();
    }

    public List<Literal> getUnsetLiterals() {
        return literalsMap.keySet().stream().filter(literal -> literal.getValue() == Literal.UNINITIATED).toList();
    }

    public Map<Literal, HashSet<Integer>> getLiteralsMap() {
        return literalsMap;
    }

    public Set<Integer> getIdClauses(Literal literal) {
        return literalsMap.get(literal);
    }

    public Map<Integer, Clause> getClauseMap() {
        return clauseMap;
    }

    public int getSatisfiable(boolean useSigns) {
        int sat = 0;
        for (Map.Entry<Integer, Clause> pair : clauseMap.entrySet()) {
            int value = pair.getValue().getSatisfiable(useSigns);
            sat += value;
            if (value == Literal.FALSE)
                return Literal.FALSE;
        }
        return sat == Literal.TRUE * clauseMap.size() ? Literal.TRUE : Literal.UNINITIATED;
    }

    @Override
    public String toString() {
        boolean hasPrev = false;
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, Clause> set : clauseMap.entrySet()) {
            if (hasPrev) {
                result.append("*");
            } else {
                hasPrev = true;
            }
            result.append(set.getValue());
        }
        return result.toString();
    }
}
