package entities;

import java.util.Objects;
import java.util.regex.Pattern;

public class Pair {
    public static final int CONJUNCTION = 0;
    public static final int DISJUNCTION = 1;
    public static final int IMPLICATION = 2;
    public static final int EQUIVALENCE = 3;
    public static final int EXCLUSIVE_DISJUNCTION = 4;

    private String literal1;
    private boolean sign1;
    private String literal2;
    private boolean sign2;
    private int type;

    public Pair(int type) {
        setType(type);
    }

    public Pair(int type, String[] disjunction) {
        setType(type);
        if (disjunction.length > 2 || disjunction[0].isEmpty()) {
            throw new IllegalArgumentException("Uses more 2 literals");
        }
        sign1 = !Pattern.matches("^[!-]\\w+$", disjunction[0]);
        if (!sign1) {
            disjunction[0] = disjunction[0].substring(1);
        }
        literal1 = disjunction[0];
        if (disjunction.length == 2) {
            if (Pattern.matches("^[!-]\\w+$", disjunction[1])) {
                sign2 = false;
                literal2 = disjunction[1].substring(1);
            } else {
                sign2 = true;
                literal2 = disjunction[1];
            }
        }
    }

    public Pair(int type, String literal1, boolean sign1) {
        setType(type);
        this.literal1 = literal1;
        this.sign1 = sign1;
    }

    public Pair(int type, String literal1, boolean sign1, String literal2, boolean sign2) {
        setType(type);
        this.literal1 = literal1;
        this.sign1 = sign1;
        if (!(literal1.equals(literal2) && sign1 == sign2)) {
            this.literal2 = literal2;
            this.sign2 = sign2;
        }
    }

    public boolean isDoable() {
        return !(literal1.equals(literal2) && sign1 == sign2);
    }

    public boolean isDoable(boolean value1) {
        value1 = value1 != sign1;
        if (literal2.isEmpty()) {
            switch (type) {
                case CONJUNCTION -> {
                    return value1 && sign2;
                }
                case DISJUNCTION -> {
                    return value1 || sign2;
                }
                case IMPLICATION -> {
                    return !(value1 && !sign2);
                }
                case EQUIVALENCE -> {
                    return value1 == sign2;
                }
                case EXCLUSIVE_DISJUNCTION -> {
                    return value1 ^ sign2;
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
        throw new IllegalArgumentException("Unknown type operation " + type);
    }

    public boolean isDoable(boolean value1, boolean value2) {
        value1 = value1 != sign1;
        value2 = value2 != sign2;
        switch (type) {
            case CONJUNCTION -> {
                return value1 && value2;
            }
            case DISJUNCTION -> {
                return value1 || value2;
            }
            case IMPLICATION -> {
                return !(value1 && !value2);
            }
            case EQUIVALENCE -> {
                return value1 == value2;
            }
            case EXCLUSIVE_DISJUNCTION -> {
                return value1 ^ value2;
            }
        }
        throw new IllegalArgumentException("Unknown type operation " + type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        if (type < 0 || type > 4) {
            throw new IllegalArgumentException("Unknown type pair " + type);
        }
        this.type = type;
    }

    public boolean hasLiteral2() {
        return !literal2.isEmpty();
    }

    public String getLiteral1() {
        return literal1;
    }

    public boolean isSign1() {
        return sign1;
    }

    public String getLiteral2() {
        return literal2;
    }

    public boolean isSign2() {
        return sign2;
    }

    public void addLiteral1(String literal1, boolean sign1) {
        this.literal1 = literal1;
        this.sign1 = sign1;
    }

    public void addLiteral2(String literal2, boolean sign2) {
        if (!(literal1.equals(literal2) && sign1 == sign2)) {
            this.literal2 = literal2;
            this.sign2 = sign2;
        }
    }

    public void removeLiteral2() {
        literal2 = null;
        sign2 = true;
    }

    public void invertSign1() {
        sign1 = !sign1;
    }

    public void invertSign2() {
        sign2 = !sign2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair that = (Pair) o;
        return sign1 == that.sign1 && sign2 == that.sign2 && Objects.equals(literal1, that.literal1) && Objects.equals(literal2, that.literal2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literal1, sign1, literal2, sign2);
    }
}