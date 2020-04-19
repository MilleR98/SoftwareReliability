package org.miller.service;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.InvalidEdgeException;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import groovy.lang.Tuple2;
import java.util.Set;
import java.util.stream.Collectors;
import org.miller.model.StateNode;

public class GraphViewService {

  private static Integer counter;

  public void fillGraphForView(Digraph<StateNode, String> graph, StateNode rootState) {

    graph.vertices().forEach(graph::removeVertex);
    graph.edges().forEach(graph::removeEdge);

    counter = 1;
    buildParts(Set.of(rootState), graph);
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
    }catch (InvalidEdgeException ignored){

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
