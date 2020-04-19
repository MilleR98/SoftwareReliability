package org.miller.service;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.InvalidEdgeException;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import groovy.lang.Tuple2;
import java.util.Set;
import java.util.stream.Collectors;
import org.miller.engine.SystemGraphComposer;
import org.miller.model.StateNode;

public class GraphViewService {

  private static Integer counter;
  private final SystemGraphComposer systemGraphComposer = new SystemGraphComposer();

  public SmartGraphPanel<StateNode, String> createGraphView(String elementsSchemaEquation) {

    counter = 1;

    var graph = new DigraphEdgeList<StateNode, String>();
    var rootNode = systemGraphComposer.buildSystemStatesGraph(elementsSchemaEquation);
    buildParts(Set.of(rootNode), graph);

    var smartGraphProperties = new SmartGraphProperties(getClass().getClassLoader().getResourceAsStream("smartgraph.properties"));
    var graphView = new SmartGraphPanel<>(graph, smartGraphProperties, new SmartCircularSortedPlacementStrategy());
    graphView.getStylesheets().add(getClass().getClassLoader().getResource("smartgraph.css").toExternalForm());
    graphView.setAutomaticLayout(true);

    graph.vertices().stream()
        .filter(stateNodeVertex -> !stateNodeVertex.element().isWorking())
        .forEach(stateNodeVertex -> graphView.getStylableVertex(stateNodeVertex).setStyleClass("not-working-state"));

    return graphView;
  }

  private void buildParts(Set<StateNode> nodes, Digraph<StateNode, String> graph) {

    for (StateNode stateNode : nodes) {

      tryInsertVertex(graph, stateNode);
    }

    for (StateNode stateNode : nodes) {

      for (var outcomingEdge : stateNode.getOutcomingEdges()) {

        tryInsertVertex(graph, outcomingEdge.getV2());

        tryInsertEdge(graph, stateNode, outcomingEdge);
      }

      buildParts(stateNode.getOutcomingEdges().stream().map(Tuple2::getV2).collect(Collectors.toSet()), graph);
    }
  }

  private void tryInsertEdge(Digraph<StateNode, String> graph, StateNode n, Tuple2<String, StateNode> outcomingEdge) {
    try {

      graph.insertEdge(n, outcomingEdge.getV2(), getEdgeLabel(n, outcomingEdge));
    } catch (InvalidEdgeException ignored) {

    }
  }

  private String getEdgeLabel(StateNode n, Tuple2<String, StateNode> outcomingEdge) {

    return "(" + n.getId() + "->" + outcomingEdge.getV2().getId() + ") " + outcomingEdge.getV1();
  }

  private void tryInsertVertex(Digraph<StateNode, String> graph, StateNode stateNode) {

    try {

      graph.insertVertex(stateNode);
      stateNode.setId(counter);
      counter += 1;
    } catch (InvalidVertexException ignored) {

    }
  }
}
