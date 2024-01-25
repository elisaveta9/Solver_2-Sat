package solvers;

import entities.core.Literal;

import java.util.List;

public interface HasSolution {
    List<Literal> getSolution();
}
