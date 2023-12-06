package entities;

import java.util.regex.Pattern;

public class Disjunction {
    private String literal1;
    private boolean sign1;
    private String literal2;
    private boolean sign2;

    public Disjunction() {
    }

    public Disjunction(String[] disjunction) {
        if (disjunction.length > 2 || disjunction[0].isEmpty()) {
            throw new IllegalArgumentException("Uses more 2 literals");
        }
        if (Pattern.matches("^[!-]\\w+$", disjunction[0])) {
            sign1 = false;
            literal1 = disjunction[0].substring(1);
        } else {
            sign1 = true;
            literal1 = disjunction[0];
        }
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

    public Disjunction(String literal1, boolean sign1) {
        this.literal1 = literal1;
        this.sign1 = sign1;
    }

    public Disjunction(String literal1, boolean sign1, String literal2, boolean sign2) {
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
    }

    public void setSign1(boolean sign1) {
        this.sign1 = sign1;
    }

    public void setSign2(boolean sign2) {
        this.sign2 = sign2;
    }
}
