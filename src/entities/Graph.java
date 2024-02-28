package entities;

import entities.core.Clause;
import entities.core.Disjunction;
import entities.core.Literal;
import entities.core.SingleLiteral;

import java.util.*;

public class Graph {
    private Map<String, HashSet<String>> graph = new TreeMap<>();

    public Graph(Map<String, HashSet<String>> graph) {
        this.graph = graph;
    }

    public Graph(ConjunctiveNormalForm cnf) {
        Map<Integer, Clause> clauseMap = cnf.getClauseMap();
        for (Clause clause : clauseMap.values()) {
            if (clause instanceof SingleLiteral) {
                Literal literal = clause.getLiteral(0, true);
                addVertexValue(
                        graph,
                        Literal.invertLiteral(literal).toString(),
                        literal.toString()
                );
            } else if (clause.getLiterals().size() == 2 && clause instanceof Disjunction) {
                Literal literal1 = clause.getLiteral(0, true),
                        literal2 = clause.getLiteral(1, true);
                addVertexValue(
                        graph,
                        Literal.invertLiteral(literal1).toString(),
                        literal2.toString());
                addVertexValue(
                        graph,
                        Literal.invertLiteral(literal2).toString(),
                        literal1.toString());

            }
        }
    }

    private void addVertexValue(Map<String, HashSet<String>> map, String from, String to) {
        if (from != null) {
            HashSet<String> values = new HashSet<>();
            if (map.containsKey(from)) {
                values = map.get(from);
            }
            if (to != null) {
                values.add(to);
            }
            map.put(
                    from,
                    values
            );
        }
    }

    public Map<String, HashSet<String>> getGraph() {
        return graph;
    }

    public Graph transponeGraph() {
        Map<String, HashSet<String>> gt = new TreeMap<>();
        for (Map.Entry<String, HashSet<String>> set : graph.entrySet()) {
            String l1 = set.getKey();
            HashSet<String> literals = new HashSet<>(set.getValue());
            for (String l2 : literals) {
                addVertexValue(gt, l2, l1);
            }
        }
        return new Graph(gt);
    }

    public int getSize() {
        return graph.size();
    }

    public HashSet<String> getVertexes() {
        HashSet<String> vertexes = new HashSet<>();
        for(Map.Entry<String, HashSet<String>> v : graph.entrySet()) {
            vertexes.add(v.getKey());
            vertexes.addAll(v.getValue());
        }
        return vertexes;
    }

    public HashSet<String> getVertexes(String literal) {
        if (!graph.containsKey(literal))
            return new HashSet<>();
        return graph.get(literal);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, HashSet<String>> set : graph.entrySet()) {
            String l1 = set.getKey();
            HashSet<String> literals = new HashSet<>(set.getValue());
            for (String l2 : literals) {
                result.append(l1).append("->").append(l2).append("\n");
            }
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph1 = (Graph) o;
        return Objects.equals(graph, graph1.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph);
    }
}