import entities.ConjunctiveNormalForm;
import solvers.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        /*Scanner in = new Scanner(System.in);
        String question = in.nextLine();
        String[] cls = Arrays.stream(question.split("(\\()|(\\))|(\\*)|(/\\\\)")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new);
        System.out.println(String.join(" ", cls));*/
        /*List<Literal1> literals1 = new ArrayList<>(), literals2 = new ArrayList<>();
        Literal1 l1 = new Literal1("!x1"), l2 = new Literal1("x2"), l3 = new Literal1("!x3");
        literals1.add(l1); literals1.add(l2); literals1.add(l3);
        literals2.add(l2); literals2.add(l3); literals2.add(l1);
        List<Literal1> r = literals1.stream().sorted().toList(),
                l = literals2.stream().sorted().toList();
        System.out.println(r.equals(l));
        literals1.sort(Comparator.comparing(Literal1::toString));
        literals2.sort(Comparator.comparing(Literal1::toString));
        System.out.println(literals1.equals(literals2));*/
        /*Literal1 n = new Literal1("f", 1);
        System.out.println(n.sign);*/
        Scanner in = new Scanner(System.in);
        //String question = in.nextLine();
        //ConjunctiveNormalForm con = new ConjunctiveNormalForm("(X0+!X1)*(X1+X2)*(X0+X3)*(X2+!X3)*(X1+!X4)*(X2+!X5)*(X1+!X5)*(!X2+!X5)*(!X3+!X6)*(!X0+X7)*(!X4+!X7)*(X5+!X7)*(!X4+!X8)*(!X7+!X8)*(!X7+X8)*(X1+!X8)*(X0+!X8)");
        //ConjunctiveNormalForm que = new ConjunctiveNormalForm(question);
        ConjunctiveNormalForm que = GeneratorCnf.generate(8, 16);
        System.out.println(que);
        ConjunctiveNormalFormSolver solver = new DpllSolver(que);
        System.out.println("DpllSolver: " + solver.solve());
        solver = new MonteCarloMethodSolver(que);
        System.out.println("MonteCarloMethodSolver: " + solver.solve());
        System.out.println(solver);
        solver = new StronglyConnectedComponentsSolver(que);
        System.out.println("StronglyConnectedComponentsSolver: " + solver.solve());
        solver = new BranchAndBoundSolver(que);
        System.out.println("BranchAndBoundSolver: " + solver.solve());
        solver = new ResolutionAndTransitiveSolution(que);
        System.out.println("ResolutionAndTransitiveSolution: " + solver.solve());
        System.out.println(solver.toString());
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
