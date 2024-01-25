package entities.core;

import java.util.Objects;

public class Literal implements Comparable<Literal> {
    public static final int UNINITIATED = 0;
    public static final int TRUE = 1;
    public static final int FALSE = 2;

    public String name;
    public int value;

    public Literal(Literal literal) {
        name = literal.name;
        value = literal.value;
    }

    public Literal(String name) {
        this.name = name;
        this.value = UNINITIATED;
    }

    public Literal(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue(boolean sign) {
        if (value == UNINITIATED) {
            return value;
        }
        if ((sign && value == TRUE) || (!sign && value == FALSE)) {
            return TRUE;
        }
        if ((sign && value == FALSE) || (!sign && value == TRUE)) {
            return FALSE;
        }
        throw new IllegalArgumentException("Unknown literal value " + value);
    }

    public void setValue(int value) {
        if (value != UNINITIATED && value != TRUE && value != FALSE)
            throw new IllegalArgumentException();
        this.value = value;
    }

    public String toString(boolean sign) {
        return sign ? name : "!" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Literal literal = (Literal) o;
        return value == literal.value && Objects.equals(name, literal.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public int compareTo(Literal o) {
        return this.toString().compareTo(o.toString());
    }
}
