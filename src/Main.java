import entities.ConjunctiveNormalForm;
import solvers.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /*System.out.println(c1.getAllLiteralsExcept(c2.getLiterals()));
        System.out.println(c1);*/
        ConjunctiveNormalForm cnf = GeneratorCnf.generate(8, 15);
        /*ConjunctiveNormalForm cnf = ConjunctiveNormalForm.parseCnf(
                "(X4+X1)*(!X0+!X4)*(X1+X6)*(X3+X4)*(!X3+!X4)*(X0+!X1)*(!X3+X3)*(X5+!X7)*(!X7+X5)*(!X1+!X0)*(X5+X3)*X6*(X1+!X1)*(X1+!X2)*(X6+X0)",
                true
        );*/
        System.out.println(cnf);
        System.out.println(MonteCarloMethodSolver.solve(cnf));
        System.out.println(cnf.getLiteralsMap());
        System.out.println(cnf.getClauseMap());
        ConjunctiveNormalForm cnf3 = new ConjunctiveNormalForm(cnf);
        System.out.println(BranchAndBoundSolver.solve(cnf3));
        ConjunctiveNormalForm cnf4 = new ConjunctiveNormalForm(cnf);
        System.out.println(DpllSolver.solve(cnf4));
        System.out.println(StronglyConnectedComponentsSolver.solve(new ConjunctiveNormalForm(cnf)));
    }
}
