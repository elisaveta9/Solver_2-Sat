package solvers;

import entities.ConjunctiveNormalForm;
import entities.Graph;

import java.util.*;

public class StronglyConnectedComponentsSolver implements ConjunctiveNormalFormSolver{
    private final Graph graph;
    private final Graph gt;
    private final HashMap<String, Boolean> used = new HashMap<>();
    private final TreeMap<String, Integer> components = new TreeMap<>();
    private int current_component = 0;
    private Deque<String> queue = new LinkedList<>();

    public StronglyConnectedComponentsSolver(ConjunctiveNormalForm cnf) {
        graph = new Graph(cnf);
        gt = graph.transponeGraph();
        for(String literal : graph.getVertexes()) {
            used.put(literal, false);
            components.put(literal, current_component);
        }
        current_component++;
    }

    @Override
    public boolean solve() {
        for (String l : gt.getVertexes()) {
            if (!used.get(l))
                dfs1(l);
        }
        while (!queue.isEmpty()) {
            String vertex = queue.pollLast();
            if (components.get(vertex) == 0) {
                dfs2(vertex);
                current_component++;
            }
        }
        for (String literal : components.keySet()) {
            if (literal.charAt(0) != '!' &&
                    components.get(literal).equals(components.get("!" + literal))) {
                return false;
            }
        }
        return true;
    }

    private void dfs1(String v) {
        used.put(v, true);
        for (String to : graph.getVertexes(v)) {
            if (!used.get(to)) {
                dfs1(to);
            }
        }
        queue.add(v);
    }

    private void dfs2(String v) {
        components.put(v, current_component);
        for (String l : gt.getVertexes(v)) {
            if (components.get(l) == 0) {
                dfs2(l);
            }
        }
    }
}
