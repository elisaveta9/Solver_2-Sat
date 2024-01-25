package entities.core;

import java.util.Objects;
import java.util.regex.Pattern;

public class PairWithValue {
    public static final int CONJUNCTION = 0;
    public static final int DISJUNCTION = 1;
    public static final int IMPLICATION = 2;
    public static final int EQUIVALENCE = 3;
    public static final int EXCLUSIVE_DISJUNCTION = 4;

    private Literal literal1 = new Literal("", true);
    private Literal literal2 = null;
    private int type;

    public PairWithValue(int type) {
        setType(type);
    }

    public PairWithValue(int type, String[] disjunction) {
        setType(type);
        if (disjunction.length > 2 || disjunction[0].isEmpty()) {
            throw new IllegalArgumentException("Uses more 2 literals");
        }
        literal1.sign = !Pattern.matches("^[!-]\\w+$", disjunction[0]);
        if (!literal1.sign) {
            disjunction[0] = disjunction[0].substring(1);
        }
        literal1.name = disjunction[0];
        if (disjunction.length == 2) {
            boolean sign2 = !Pattern.matches("^[!-]\\w+$", disjunction[1]);
            if (!sign2) {
                disjunction[1] = disjunction[1].substring(1);
            }
            addLiteral2(disjunction[1], sign2);
        }
    }

    public PairWithValue(int type, String literal1, boolean sign1) {
        setType(type);
        this.literal1.name = literal1;
        this.literal1.sign = sign1;
    }

    public PairWithValue(int type, String literal1, boolean sign1, String literal2, boolean sign2) {
        setType(type);
        addLiteral1(literal1, sign1);
        if (!(literal1.equals(literal2) && sign1 == sign2)) {
            addLiteral2(literal2, sign2);
        }
    }

    public PairWithValue(Literal literal1, Literal literal2, int type) {
        this.literal1 = literal1;
        this.literal2 = literal2;
        this.type = type;
    }

    public int getSatisfiable() {
        if (literal1.getValue() == Literal.UNINITIATED && literal2.getValue() == Literal.UNINITIATED) {
            return Literal.UNINITIATED;
        }
        switch (type) {
            case CONJUNCTION -> {
                int value1 = literal1.getValue(), value2 = literal2.getValue();
                if (value1 == Literal.TRUE && value2 == Literal.TRUE) {
                    return Literal.TRUE;
                }
                if (value1 == Literal.FALSE || value2 == Literal.FALSE) {
                    return Literal.FALSE;
                }
                return Literal.UNINITIATED;
            }
            case DISJUNCTION -> {
                int value1 = literal1.getValue(), value2 = literal2.getValue();
                if (value1 == Literal.TRUE || value2 == Literal.TRUE ||
                        (value1 == Literal.UNINITIATED && !literal1.sign) ||
                        (value2 == Literal.UNINITIATED && !literal2.sign)) {
                    return Literal.TRUE;
                }
                if (value1 == Literal.FALSE && value2 == Literal.FALSE) {
                    return Literal.FALSE;
                }
                return Literal.UNINITIATED;
            }
            case IMPLICATION -> {
                int value1 = literal1.getValue(), value2 = literal2.getValue();
                if (value1 == Literal.TRUE && value2 == Literal.FALSE) {
                    return Literal.FALSE;
                }
                int answer = Literal.TRUE;
                if ((value1 == Literal.TRUE && value2 == Literal.UNINITIATED)
                    || (value1 == Literal.UNINITIATED && value2 == Literal.FALSE)) {
                    answer = Literal.UNINITIATED;
                }
                return  answer;
            }
            case EQUIVALENCE -> {
                int value1 = literal1.getValue(), value2 = literal2.getValue();
                if (value1 == value2 && value1 != Literal.UNINITIATED) {
                    return Literal.TRUE;
                }
                if (value1 != value2 && !(value1 == Literal.UNINITIATED || value2 == Literal.UNINITIATED)) {
                    return Literal.FALSE;
                }
                return Literal.UNINITIATED;
            }
            case EXCLUSIVE_DISJUNCTION -> {
                int value1 = literal1.getValue(), value2 = literal2.getValue();
                if (value1 == value2 && value1 != Literal.UNINITIATED) {
                    return Literal.FALSE;
                }
                if (value1 != value2 && !(value1 == Literal.UNINITIATED || value2 == Literal.UNINITIATED)) {
                    return Literal.TRUE;
                }
                return Literal.UNINITIATED;
            }
            default -> {
                throw new RuntimeException("Unknown pair' type " + type);
            }
        }
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
        return !literal2.name.isEmpty();
    }

    public boolean getSign(String literal) {
        if (literal.equals(literal1.name)) {
            return literal1.sign;
        } else if (literal.equals(literal2.name)) {
            return literal2.sign;
        }
        throw new IllegalArgumentException("Unknown literal" + literal);
    }

    public Literal getLiteral1() {
        return literal1;
    }

    public boolean isSign1() {
        return literal1.sign;
    }

    public Literal getLiteral2() {
        return literal2;
    }

    public boolean isSign2() {
        return literal2.sign;
    }

    public void addLiteral1(String literal1, boolean sign1) {
        this.literal1 = new Literal(literal1, sign1);
    }

    public void addLiteral1(Literal literal) {
        this.literal1 = literal;
    }

    public void addLiteral2(String literal2, boolean sign2) {
        if (literal1 == null) {
            addLiteral1(literal2, sign2);
            return;
        }
        this.literal2 = new Literal(literal2, sign2);
    }

    public void addLiteral2(Literal literal) {
        if (literal1 == null) {
            addLiteral1(literal);
            return;
        }
        this.literal2 = literal;
    }

    public void removeLiteral2() {
        literal2 = null;
    }

    public void invertSign1() {
        literal1.sign = !literal1.sign;
    }

    public void invertSign2() {
        literal2.sign = !literal2.sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairWithValue that = (PairWithValue) o;
        return type == that.type && Objects.equals(literal1, that.literal1) && Objects.equals(literal2, that.literal2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literal1, literal2, type);
    }

    @Override
    public String toString() {
        switch (type) {
            case CONJUNCTION -> {
                return "(" + literal1.toString() + "*" + literal2.toString() + ")";
            }
            case DISJUNCTION -> {
                return "(" + literal1.toString() + "+" + literal2.toString() + ")";
            }
            case IMPLICATION -> {
                return "(" + literal1.toString() + "->" + literal2.toString() + ")";
            }
            case EQUIVALENCE -> {
                return "(" + literal1.toString() + "<->" + literal2.toString() + ")";
            }
            case EXCLUSIVE_DISJUNCTION -> {
                return "(" + literal1.toString() + "XOR" + literal2.toString() + ")";
            }
            default -> throw new IllegalArgumentException("Unknown type " + type);
        }
    }
}
