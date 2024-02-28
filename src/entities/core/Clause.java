package entities.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Clause {
    private List<Literal> literals = new ArrayList<>();
    private List<Integer> signs = new ArrayList<>();

    public Clause() {}

    public Clause(String[] src) {
        for (String item : src) {
            Literal literal = new Literal(item);
            literals.add(literal);
            signs.add(literal.getSign());
        }
    }

    public Clause(Literal... literals) {
        this.literals = Arrays.stream(literals).toList();
        for (Literal item : literals) {
            signs.add(item.getSign());
        }
    }

    public Clause(List<Literal> literals) {
        this.literals = new ArrayList<>(literals);
        for (Literal item : literals) {
            signs.add(item.getSign());
        }
    }

    public Clause(List<Literal> literals, List<Integer> signs) {
        if (literals.size() != signs.size()) {
            throw new IllegalArgumentException();
        }
        this.literals = new ArrayList<>(literals);
        this.signs = new ArrayList<>(signs);
    }

    public Clause(Clause src) {
        for (Literal literal : src.literals) {
            literals.add(new Literal(literal, false));
        }
        signs = new ArrayList<>(src.signs);
    }

    public List<Literal> getLiterals() {
        return literals;
    }

    public List<Literal> getUnsetValueLiterals() {
        return literals.stream().filter(literal -> literal.getSign() == Literal.UNINITIATED).toList();
    }

    public void setLiterals(Literal... literals) {
        this.literals = Arrays.stream(literals).toList();
    }

    public void setLiterals(List<Literal> literals) {
        this.literals = new ArrayList<>(literals);
    }

    public List<Integer> getSigns() {
        return signs;
    }

    public boolean addLiteral(Literal l) {
        literals.add(l);
        signs.add(l.getSign());
        return true;
    }

    public boolean addLiteral(Literal l, Integer sign) {
        literals.add(l);
        signs.add(sign);
        return true;
    }

    public boolean addLiteral(int index, Literal l) {
        if (index > literals.size() || index < 0)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        literals.add(index, l);
        if (index < signs.size()) {
            signs.add(index, l.getSign());
        }
        return true;
    }

    public boolean addLiteral(int index, Literal l, Integer sign) {
        if (index > literals.size() || index < 0)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        literals.add(index, l);
        if (index < signs.size()) {
            signs.add(index, sign);
        }
        return true;
    }

    public void clear() {
        literals = new ArrayList<>();
        signs = new ArrayList<>();
    }

    public boolean removeLiteral(Literal l) {
        boolean result = false;
        for (int i = literals.size() - 1; i >= 0; --i) {
            if (literals.get(i).equals(l)) {
                literals.remove(i);
                if (signs.size() > i) signs.remove(i);
                result = true;
            }
        }
        return result;
    }

    public List<Literal> getAllLiteralsExcept(Literal... exceptLiterals) {
        return getAllLiteralsExcept(Arrays.stream(exceptLiterals).toList());
    }

    public List<Literal> getAllLiteralsExcept(List<Literal> exceptLiterals) {
        return literals.stream()
                .filter(e -> !exceptLiterals.contains(e))
                .collect(Collectors.toList());
    }

    public Literal getLiteral(int index) {
        if (index < 0 || index >= literals.size())
            throw new IndexOutOfBoundsException();
        return literals.get(index);
    }

    public Literal getLiteral(int index, boolean useSignsList) {
        if (useSignsList && index < signs.size() && index >= 0) {
            Literal literal = new Literal(getLiteral(index));
            literal.setSign(signs.get(index));
            return literal;
        }
        return getLiteral(index);
    }

    public int getIndex(Literal literal) {
        return literals.indexOf(literal);
    }

    public Integer getSign(Literal literal) {
        int index = literals.indexOf(literal);
        if (index == -1)
            return -1;
        if (signs.size() < index)
            return literal.getSign();
        return signs.get(index);
    }

    public List<Integer> getSignsLiteral(Literal literal) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < literals.size(); ++i) {
            if (literals.get(i).equals(literal))
                if (signs.size() > i) {
                    result.add(signs.get(i));
                } else {
                    result.add(literal.getSign());
                }
        }
        return result;
    }

    public abstract boolean isSatisfiable(boolean useSigns);

    public abstract int getSatisfiable(boolean useSigns);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Clause clause = (Clause) o;
        if (Objects.equals(literals.stream().sorted().toList(),
                clause.literals.stream().sorted().toList())) {
            for (Literal l : literals) {
                if (!getSign(l).equals(clause.getSign(l)))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(literals);
    }
}
