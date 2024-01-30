package entities.core;

public class LiteralWithSign extends Literal {
    boolean sign;

    public LiteralWithSign(Literal literal, boolean sign) {
        super(literal);
        this.sign = sign;
    }

    public LiteralWithSign(String name, boolean sign) {
        super(name);
        this.sign = sign;
    }

    public LiteralWithSign(String name, int value, boolean sign) {
        super(name, value);
        this.sign = sign;
    }

    public int getValue() {
        return super.getValue(sign);
    }

    public void invertSign() {
        sign = !sign;
    }

    public boolean getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return super.toString(sign);
    }
}
