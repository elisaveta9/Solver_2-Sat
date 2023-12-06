package entities;

public class Disjunction {
    private final String literal1;
    private boolean sign1;
    private String literal2;
    private boolean sign2;

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
