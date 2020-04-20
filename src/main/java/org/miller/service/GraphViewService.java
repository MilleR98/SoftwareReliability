package org.miller.service;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidEdgeException;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import groovy.lang.Tuple2;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.miller.engine.SystemGraphComposer;
import org.miller.model.NodeEquation;
import org.miller.model.StateEdge;
import org.miller.model.StateNode;

public class GraphViewService {

  private static Integer nodeIdCounter;
  private final SystemGraphComposer systemGraphComposer = new SystemGraphComposer();

  public Tuple2<SmartGraphPanel<StateNode, StateEdge>, DigraphEdgeList<StateNode, StateEdge>> createGraphView(String elementsSchemaEquation) {

    nodeIdCounter = 1;

    var graph = new DigraphEdgeList<StateNode, StateEdge>();
    var rootNode = systemGraphComposer.buildSystemStatesGraph(elementsSchemaEquation);
    buildParts(Set.of(rootNode), graph);

    var smartGraphProperties = new SmartGraphProperties(getClass().getClassLoader().getResourceAsStream("smartgraph.properties"));
    var graphView = new SmartGraphPanel<>(graph, smartGraphProperties, new SmartCircularSortedPlacementStrategy());
    graphView.getStylesheets().add(getClass().getClassLoader().getResource("smartgraph.css").toExternalForm());
    graphView.setAutomaticLayout(true);

    graph.vertices().stream()
        .filter(stateNodeVertex -> !stateNodeVertex.element().isWorking())
        .forEach(stateNodeVertex -> graphView.getStylableVertex(stateNodeVertex).setStyleClass("not-working-state"));

    return new Tuple2<>(graphView, graph);
  }

  private void buildParts(Set<StateNode> nodes, Digraph<StateNode, StateEdge> graph) {

    for (StateNode stateNode : nodes) {

      tryInsertVertex(graph, stateNode);
    }

    for (StateNode stateNode : nodes) {

      for (var outcomingEdge : stateNode.getOuboundEdges()) {

        tryInsertVertex(graph, outcomingEdge.getV2());

        tryInsertEdge(graph, stateNode, outcomingEdge);
      }

      buildParts(stateNode.getOuboundEdges().stream().map(Tuple2::getV2).collect(Collectors.toSet()), graph);
    }
  }

  private void tryInsertEdge(Digraph<StateNode, StateEdge> graph, StateNode n, Tuple2<StateEdge, StateNode> outcomingEdge) {
    try {

      if(!(n.getId() == 0 && outcomingEdge.getV2().getId() == 0)){

        outcomingEdge.getV1().setLabel("(" + n.getId() + "->" + outcomingEdge.getV2().getId() + ") ");
        graph.insertEdge(n, outcomingEdge.getV2(), outcomingEdge.getV1());
      }

    } catch (InvalidEdgeException ignored) {

    }
  }

  private void tryInsertVertex(Digraph<StateNode, StateEdge> graph, StateNode stateNode) {

    try {

      graph.insertVertex(stateNode);
      stateNode.setId(nodeIdCounter);
      nodeIdCounter += 1;
    } catch (InvalidVertexException ignored) {

    }
  }

  public List<NodeEquation> getNodeEquations(DigraphEdgeList<StateNode, StateEdge> digraph) {

    List<NodeEquation> nodeEquations = new ArrayList<>();
    for (Vertex<StateNode> vertex : digraph.vertices().stream().sorted(Comparator.comparingInt(v -> v.element().getId())).collect(Collectors.toList())) {

      var equation = new StringBuilder("P" + vertex.element().getId() + "(t)/dt = ");

      var inboundEdges = digraph.incidentEdges(vertex);

      int counter = 0;
      for (Edge<StateEdge, StateNode> edge : inboundEdges) {

        if (counter != 0) {

          equation.append("+");
        }

        equation.append(edge.element().getValue()).append("*P").append(edge.vertices()[0].element().getId()).append("(t)");

        ++counter;
      }

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

        equation.append(")*P").append(vertex.element().getId()).append("(t)");
      }

      nodeEquations.add(new NodeEquation(vertex.element().getId(), equation.toString(), vertex.element().isWorking() ? "Works" : "Fail"));
    }

    nodeEquations.add(new NodeEquation(0, "", ""));
    nodeEquations.add(new NodeEquation(0, "Probability of failure-free operation:", ""));

    var worksNodes = nodeEquations.stream().filter(nodeEquation -> nodeEquation.getStatus().equals("Works")).collect(Collectors.toList());

    var failureFreeOperationEquation = new StringBuilder("P(t) = ");

    int counter = 0;
    for (var worksNode : worksNodes) {

      if (counter != 0) {

        failureFreeOperationEquation.append("+");
      }

      failureFreeOperationEquation.append("P").append(worksNode.getId()).append("(t)");

      ++counter;
    }
    nodeEquations.add(new NodeEquation(0, failureFreeOperationEquation.toString(), ""));

    return nodeEquations;
  }
}
