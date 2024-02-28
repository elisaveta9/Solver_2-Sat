package entities.core;

import java.util.Objects;
import java.util.regex.Pattern;

public class Literal implements Comparable<Literal> {
    public static final int UNINITIATED = 0;
    public static final int TRUE = 1;
    public static final int FALSE = 2;
    public String name;
    private Integer value;
    private Integer sign;

    public Literal(String src) {
        parse(src);
        value = UNINITIATED;
    }

    public Literal(Literal literal) {
        name = literal.name;
        value = literal.value;
        sign = literal.sign;
    }

    public Literal(Literal literal, boolean saveValue) {
        name = literal.name;
        value = saveValue ? literal.value : UNINITIATED;
        sign = literal.sign;
    }

    public Literal(String src, int value) {
        parse(src);
        this.value = value;
    }

    public Literal(String name, int value, boolean sign) {
        parse(name);
        this.sign = sign ? TRUE : FALSE;
        this.value = value;
    }

    public Literal(String name, int value, int sign) {
        parse(name);
        this.sign = sign;
        this.value = value;
    }

    public void setValue(Integer value) {
        if (value != UNINITIATED && value != TRUE && value != FALSE)
            throw new IllegalArgumentException("Unknown value: " + value);
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public void setSign(Integer sign) {
        if (sign != UNINITIATED && sign != TRUE && sign != FALSE)
            throw new IllegalArgumentException("Unknown value: " + sign);
        this.sign = sign;
    }

    public Integer getSign() {
        return sign;
    }

    public Integer isSatisfiable() {
        return isSatisfiable(sign, value);
    }

    public Integer isSatisfiable(int sign, int value) {
        switch (value) {
            case UNINITIATED -> {
                return UNINITIATED;
            }
            case TRUE -> {
                return sign != FALSE ? TRUE : FALSE;
            }
            case FALSE -> {
                return sign != FALSE ? FALSE : TRUE;
            }
            default -> throw new RuntimeException("Unknown value: " + value);
        }
    }

    private void parse(String src) {
        sign = Pattern.matches("^[-!][a-zа-яA-ZА-Я\\d]+$", src) ? FALSE :
                Pattern.matches("^[a-zа-яA-ZА-Я\\d]+$", src) ? TRUE : UNINITIATED;
        if (sign == UNINITIATED) {
            throw new IllegalArgumentException("Wrong literal: " + src);
        }
        name = sign == TRUE ? src : src.substring(1);
    }

    public static Literal invertLiteral(Literal literal) {
        Literal result = new Literal(literal);
        result.sign = literal.sign == FALSE ? TRUE : FALSE;
        return result;
    }

    @Override
    public String toString() {
        return sign == FALSE ? "!" + name : name;
    }

    public String toString(Integer sign) {
        return sign == FALSE ? "!" + name : name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Literal literal1 = (Literal) o;
        return Objects.equals(name, literal1.name) && Objects.equals(value, literal1.value) && Objects.equals(sign, literal1.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, sign);
    }

    @Override
    public int compareTo(Literal o) {
        return this.toString().compareTo(o.toString());
    }
}
