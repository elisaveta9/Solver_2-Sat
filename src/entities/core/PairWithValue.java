package entities.core;

import java.util.Objects;
import java.util.regex.Pattern;

public class PairWithValue {
    public static final int CONJUNCTION = 0;
    public static final int DISJUNCTION = 1;
    public static final int IMPLICATION = 2;
    public static final int EQUIVALENCE = 3;
    public static final int EXCLUSIVE_DISJUNCTION = 4;

    private Literal literal1 = new Literal("");
    private boolean sign1 = true;
    private Literal literal2 = null;
    private boolean sign2 = true;
    private int type;

    public PairWithValue(int type) {
        setType(type);
    }

    public PairWithValue(int type, String[] disjunction) {
        setType(type);
        if (disjunction.length > 2 || disjunction[0].isEmpty()) {
            throw new IllegalArgumentException("Uses more 2 literals");
        }
        sign1 = !Pattern.matches("^[!-]\\w+$", disjunction[0]);
        if (!sign1) {
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
        this.sign1 = sign1;
    }

    public PairWithValue(int type, Literal literal1, boolean sign1) {
        setType(type);
        this.literal1 = literal1;
        this.sign1 = sign1;
    }

    public PairWithValue(int type, String literal1, boolean sign1, String literal2, boolean sign2) {
        setType(type);
        addLiteral1(literal1, sign1);
        if (!(literal1.equals(literal2) && sign1 == sign2)) {
            addLiteral2(literal2, sign2);
        }
    }

    public PairWithValue(Literal literal1, boolean sign1, Literal literal2, boolean sign2, int type) {
        this.literal1 = literal1;
        this.sign1 = sign1;
        this.literal2 = literal2;
        this.sign2 = sign2;
        this.type = type;
    }

    public int getSatisfiable() {
        if (literal2 == null)
            return literal1.getValue(sign1);
        if (type != DISJUNCTION && literal1.getValue(sign1) == Literal.UNINITIATED && literal2.getValue(sign2) == Literal.UNINITIATED) {
            return Literal.UNINITIATED;
        }
        switch (type) {
            case CONJUNCTION -> {
                int value1 = literal1.getValue(sign1), value2 = literal2.getValue(sign2);
                if (value1 == Literal.TRUE && value2 == Literal.TRUE) {
                    return Literal.TRUE;
                }
                if (value1 == Literal.FALSE || value2 == Literal.FALSE) {
                    return Literal.FALSE;
                }
                return Literal.UNINITIATED;
            }
            case DISJUNCTION -> {
                int value1 = literal1.getValue(sign1), value2 = literal2.getValue(sign2);
                if (value1 == Literal.TRUE || value2 == Literal.TRUE ||
                        (value1 == Literal.UNINITIATED && !sign1) ||
                        (value2 == Literal.UNINITIATED && !sign2)) {
                    return Literal.TRUE;
                }
                if (value1 == Literal.FALSE && value2 == Literal.FALSE) {
                    return Literal.FALSE;
                }
                return Literal.UNINITIATED;
            }
            case IMPLICATION -> {
                int value1 = literal1.getValue(sign1), value2 = literal2.getValue(sign2);
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
                int value1 = literal1.getValue(sign1), value2 = literal2.getValue(sign2);
                if (value1 == value2 && value1 != Literal.UNINITIATED) {
                    return Literal.TRUE;
                }
                if (value1 != value2 && !(value1 == Literal.UNINITIATED || value2 == Literal.UNINITIATED)) {
                    return Literal.FALSE;
                }
                return Literal.UNINITIATED;
            }
            case EXCLUSIVE_DISJUNCTION -> {
                int value1 = literal1.getValue(sign1), value2 = literal2.getValue(sign2);
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
        return literal2 != null;
    }

    public boolean getSign(String literal) {
        if (literal.equals(literal1.name)) {
            return sign1;
        } else if (literal.equals(literal2.name)) {
            return sign2;
        }
        throw new IllegalArgumentException("Unknown literal" + literal);
    }

    public Literal getLiteral1() {
        return literal1;
    }

    public boolean isSign1() {
        return sign1;
    }

    public Literal getLiteral2() {
        return literal2;
    }

    public boolean isSign2() {
        return sign2;
    }

    public void addLiteral1(String literal1, boolean sign1) {
        this.literal1 = new Literal(literal1);
        this.sign1 = sign1;
    }

    public void addLiteral1(Literal literal, boolean sign) {
        this.literal1 = literal;
        sign1 = sign;
    }

    public void addLiteral2(String literal2, boolean sign2) {
        if (literal1 == null) {
            addLiteral1(literal2, sign2);
            return;
        }
        this.literal2 = new Literal(literal2);
        this.sign2 = sign2;
    }

    public void addLiteral2(Literal literal, boolean sign) {
        if (literal1 == null) {
            addLiteral1(literal, sign);
            return;
        }
        this.literal2 = literal;
        sign2 = sign;
    }

    public void setSign1(boolean sign1) {
        this.sign1 = sign1;
    }

    public void setSign2(boolean sign2) {
        this.sign2 = sign2;
    }

    public void removeLiteral2() {
        literal2 = null;
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
        PairWithValue that = (PairWithValue) o;
        return type == that.type && Objects.equals(literal1, that.literal1) && Objects.equals(literal2, that.literal2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literal1, literal2, type);
    }

    @Override
    public String toString() {
        String firstPart = literal1.toString(sign1), secondPart;
        if (!hasLiteral2()) {
            return firstPart;
        } else {
            secondPart = literal2.toString(sign2);
        }
        switch (type) {
            case CONJUNCTION -> {
                return "(" + firstPart + "*" + secondPart + ")";
            }
            case DISJUNCTION -> {
                return "(" + firstPart + "+" + secondPart + ")";
            }
            case IMPLICATION -> {
                return "(" + firstPart + "->" + secondPart + ")";
            }
            case EQUIVALENCE -> {
                return "(" + firstPart + "<->" + secondPart + ")";
            }
            case EXCLUSIVE_DISJUNCTION -> {
                return "(" + firstPart + "XOR" + secondPart + ")";
            }
            default -> throw new IllegalArgumentException("Unknown type " + type);
        }
    }
}
