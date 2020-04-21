package org.miller.service;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.miller.model.NodeEquation;
import org.miller.model.StateEdge;
import org.miller.model.StateNode;

public class EquationsBuilderService {

  public List<NodeEquation> getNodeEquations(DigraphEdgeList<StateNode, StateEdge> digraph) {

    List<NodeEquation> nodeEquations = new ArrayList<>();
    for (Vertex<StateNode> vertex : digraph.vertices().stream().sorted(Comparator.comparingInt(v -> v.element().getId())).collect(Collectors.toList())) {

      var equation = new StringBuilder("dP[" + vertex.element().getId() + "](t)/dt = ");

      addInboundBasedParams(digraph, vertex, equation);

      addOutboundBasedParams(digraph, vertex, equation);

      nodeEquations.add(new NodeEquation(vertex.element().getId(), equation.toString(), vertex.element().isWorking() ? "Works" : "Fail"));
    }

    addFailureFreeProbabilityEquation(nodeEquations);

    return nodeEquations;
  }

  private void addFailureFreeProbabilityEquation(List<NodeEquation> nodeEquations) {

    nodeEquations.add(new NodeEquation(0, "", ""));
    nodeEquations.add(new NodeEquation(0, "Probability of failure-free operation:", ""));

    var worksNodes = nodeEquations.stream().filter(nodeEquation -> nodeEquation.getStatus().equals("Works")).collect(Collectors.toList());

    var failureFreeOperationEquation = new StringBuilder("P(t) = ");

    int counter = 0;
    for (var worksNode : worksNodes) {

      if (counter != 0) {

        failureFreeOperationEquation.append("+");
      }

      failureFreeOperationEquation.append("P[").append(worksNode.getId()).append("](t)");

      ++counter;
    }

    nodeEquations.add(new NodeEquation(0, failureFreeOperationEquation.toString(), ""));
  }

  private void addOutboundBasedParams(DigraphEdgeList<StateNode, StateEdge> digraph, Vertex<StateNode> vertex, StringBuilder equation) {

    int counter;
    var outboundEdges = digraph.outboundEdges(vertex);
    if (!outboundEdges.isEmpty()) {

      equation.append("-(");
      counter = 0;
      for (Edge<StateEdge, StateNode> edge : outboundEdges) {

        if (counter != 0) {

          equation.append("+");
        }

        equation.append(edge.element().getValue());
        ++counter;
      }

      equation.append(")*P[").append(vertex.element().getId()).append("](t)");
    }
  }

  private void addInboundBasedParams(DigraphEdgeList<StateNode, StateEdge> digraph, Vertex<StateNode> vertex, StringBuilder equation) {

    var inboundEdges = digraph.incidentEdges(vertex);

    int counter = 0;
    for (Edge<StateEdge, StateNode> edge : inboundEdges) {

      if (counter != 0) {

        equation.append("+");
      }

      equation.append(edge.element().getValue()).append("*P[").append(edge.vertices()[0].element().getId()).append("](t)");

      ++counter;
    }
  }
}
