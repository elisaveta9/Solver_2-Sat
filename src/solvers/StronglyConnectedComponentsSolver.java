package solvers;

import entities.ConjunctiveNormalForm;
import entities.Graph;

import java.util.*;

public class StronglyConnectedComponentsSolver {
    public static boolean solve(ConjunctiveNormalForm cnf) {
        Graph graph = new Graph(cnf), gt = graph.transponeGraph();
        Map<String, Boolean> used = new TreeMap<>();
        Map<String, Integer> components = new TreeMap<>();
        Integer current_component = 0;
        Deque<String> queue = new LinkedList<>();
        for(String literal : graph.getVertexes()) {
            used.put(literal, false);
            components.put(literal, current_component);
        }
        current_component++;

        HashSet<String> vertexes = graph.getVertexes();
        for (String l : vertexes) {
            if (!used.get(l))
                dfs1(l, graph, used, queue);
        }
        while (!queue.isEmpty()) {
            String vertex = queue.pollLast();
            if (components.get(vertex) == 0) {
                dfs2(vertex, gt, components, current_component);
                current_component++;
            }
        }
        for (String literal : components.keySet()) {
            if (literal.charAt(0) != '!' &&
                    components.get(literal) != 0 &&
                    components.get(literal).equals(components.get("!" + literal))) {
                return false;
            }
        }
        return true;
    }

    private static void dfs1(String v, Graph g,
                             Map<String, Boolean> used, Deque<String> queue) {
        used.put(v, true);
        for (String to : g.getVertexes(v)) {
            if (!used.get(to)) {
                dfs1(to, g, used, queue);
            }
        }
        queue.add(v);
    }

    private static void dfs2(String v, Graph gt,
                             Map<String, Integer> components, Integer current_component) {
        components.put(v, current_component);
        for (String l : gt.getVertexes(v)) {
            if (components.get(l) == 0) {
                dfs2(l, gt, components, current_component);
            }
        }
    }
}
