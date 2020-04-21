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
import org.miller.model.NodeEquation;
import org.miller.model.StateEdge;
import org.miller.model.StateNode;

public class GraphViewService {

  private static Integer nodeIdCounter;
  private final GraphComposer systemGraphComposer = new GraphComposer();

  public Tuple2<SmartGraphPanel<StateNode, StateEdge>, DigraphEdgeList<StateNode, StateEdge>> createGraphView(String elementsSchemaEquation) {

    nodeIdCounter = 0;

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

      for (var outboundEdge : stateNode.getOutboundEdges()) {

        tryInsertVertex(graph, outboundEdge.getV2());

        tryInsertEdge(graph, stateNode, outboundEdge);
      }

      buildParts(stateNode.getOutboundEdges().stream().map(Tuple2::getV2).collect(Collectors.toSet()), graph);
    }
  }

  private void tryInsertEdge(Digraph<StateNode, StateEdge> graph, StateNode n, Tuple2<StateEdge, StateNode> outcomingEdge) {
    try {

      if (!(n.getId() == 0 && outcomingEdge.getV2().getId() == 0)) {

        int outId = outcomingEdge.getV2().getId();
        if (outId == 0) {
          outId = graph.vertices().stream().map(Vertex::element)
              .filter(v -> v.equals(outcomingEdge.getV2())).findFirst()
              .map(StateNode::getId).orElse(0);
        }
        outcomingEdge.getV1().setLabel("(" + n.getId() + "->" + outId + ") ");
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
}
