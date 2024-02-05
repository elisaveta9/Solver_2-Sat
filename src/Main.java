import entities.ConjunctiveNormalForm;
import solvers.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        String question = in.nextLine();
        ConjunctiveNormalForm con = new ConjunctiveNormalForm("(X0+!X1)*(X1+X2)*(X0+X3)*(X2+!X3)*(X1+!X4)*(X2+!X5)*(X1+!X5)*(!X2+!X5)*(!X3+!X6)*(!X0+X7)*(!X4+!X7)*(X5+!X7)*(!X4+!X8)*(!X7+!X8)*(!X7+X8)*(X1+!X8)*(X0+!X8)");
        ConjunctiveNormalForm que = new ConjunctiveNormalForm(question);
        ConjunctiveNormalFormSolver solver = new DpllSolver(que);
        System.out.println(solver.solve());
        solver = new BranchAndBoundSolver(que);
        System.out.println(solver.solve());
        solver = new ResolutionAndTransitiveSolution(que);
        System.out.println(solver.solve());
        System.out.println(solver);
        /*BranchAndBoundSolver solver = new BranchAndBoundSolver(que);
        System.out.println(solver);
        System.out.println(solver.solve());
        List<Literal> solution = solver.getSolution();
        for (Literal literal : solution) {
            System.out.println(literal.name + " : " + (literal.value == Literal.TRUE));
        }
        System.out.println(solver);*/
    }
}
