package entities.core;

import java.util.List;

public class SingleLiteral extends Clause {
    private static final String LITERAL_PATTERN = "[-!]?[a-zа-яA-ZА-Я\\d]+";

    public SingleLiteral() { super(); }

    public SingleLiteral(Clause clause) {
        super(clause);
        if(!(clause instanceof SingleLiteral || clause.getLiterals().size() <= 1))
            throw new IllegalArgumentException("SingleLiteral must exist only one literal!");
    }

    public SingleLiteral(Literal literal) { super(literal); }

    public SingleLiteral(Literal literal, Integer sign) {
        super();
        addLiteral(literal, sign);
    }

    @Override
    public void setLiterals(Literal... literals) {
        if (literals.length != 1) {
            throw new IllegalArgumentException("SingleLiteral must exist only one literal!");
        }
        super.setLiterals(literals);
    }

    @Override
    public void setLiterals(List<Literal> literals) {
        if (literals.size() != 1) {
            throw new IllegalArgumentException("SingleLiteral must exist only one literal!");
        }
        super.setLiterals(literals);
    }

    public static SingleLiteral parse(String src) {
        if (src.matches(LITERAL_PATTERN)) {
            Literal literal = new Literal(src);
            return new SingleLiteral(literal);
        }
        throw new IllegalArgumentException("");
    }

    @Override
    public boolean isSatisfiable(boolean useSigns) {
        return getLiterals().size() == 1 && (useSigns ?
                isSatisfiableUseSigns() : isSatisfiable());
    }

    private boolean isSatisfiable() {
        return getLiterals().get(0).isSatisfiable() != Literal.FALSE;
    }

    private boolean isSatisfiableUseSigns() {
        Literal literal = getLiterals().get(0);
        return literal.isSatisfiable(getSigns().get(0), literal.getValue()) != Literal.FALSE;
    }

    @Override
    public int getSatisfiable(boolean useSigns) {
        return getLiterals().size() != 1 ? Literal.UNINITIATED : useSigns ?
                getLiteral(0).isSatisfiable(getSigns().get(0), getLiteral(0).getValue()) :
                getLiteral(0).isSatisfiable();
    }

    @Override
    public String toString() {
        if (getLiterals().isEmpty())
            return "";
        if (getSigns().size() != getLiterals().size())
            return getLiterals().get(0).toString();
        else return getLiterals().get(0).toString(getSigns().get(0));
    }
}
