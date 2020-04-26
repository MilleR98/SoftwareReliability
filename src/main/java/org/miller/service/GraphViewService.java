package org.miller.service;

import static org.miller.model.StateNode.binaryState;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.InvalidEdgeException;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import groovy.lang.Tuple2;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.miller.model.StateEdge;
import org.miller.model.StateNode;

public class GraphViewService {

  private static Integer nodeIdCounter;
  private final GraphComposer systemGraphComposer = new GraphComposer();

  public Tuple2<SmartGraphPanel<StateNode, StateEdge>, DigraphEdgeList<StateNode, StateEdge>> createGraphView(String elementsSchemaEquation, int indexOfOneTimeRepairElement) {

    nodeIdCounter = 0;

    var graph = new DigraphEdgeList<StateNode, StateEdge>();
    var rootNode = systemGraphComposer.buildSystemStatesGraph(elementsSchemaEquation, indexOfOneTimeRepairElement);
    buildParts(Set.of(rootNode), graph);

    graph.vertices().stream()
        .filter(stateNodeVertex -> !stateNodeVertex.element().isWorking())
        .map(Vertex::element)
        .forEach(stateNode -> {

          for (int i = 0; i < stateNode.getState().length; i++) {

            if (i != stateNode.getRepairedIndex() && !stateNode.getState()[i]) {

              final int index = i;

              var copyState = Arrays.copyOf(stateNode.getState(), stateNode.getState().length);
              copyState[i] = true;

              graph.vertices().stream()
                  .map(Vertex::element)
                  .filter(node -> node.isWorking() || node.isNodeWithEdgeToRepaired())
                  .filter(node -> node.binaryState().equals(binaryState(copyState, stateNode.isNodeWithRepair(), stateNode.getRepairedIndex())))
                  .findFirst().ifPresent(node -> {

                var repairEdge = new StateEdge();
                repairEdge.setValue("µ[" + index + "]");
                repairEdge.setLabel("(" + stateNode.getId() + "->" + node.getId() + ") ");

                try {

                  graph.insertEdge(stateNode, node, repairEdge);
                } catch (InvalidEdgeException ignored) {

                }
              });
            }
          }

        });

    var smartGraphProperties = new SmartGraphProperties(getClass().getClassLoader().getResourceAsStream("smartgraph.properties"));
    var graphView = new SmartGraphPanel<>(graph, smartGraphProperties, new SmartCircularSortedPlacementStrategy());
    graphView.getStylesheets().add(getClass().getClassLoader().getResource("smartgraph.css").toExternalForm());
    //graphView.setAutomaticLayout(true);

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

  private void tryInsertEdge(Digraph<StateNode, StateEdge> graph, StateNode currentNode, Tuple2<StateEdge, StateNode> outboundEdge) {
    try {

      if (!(currentNode.getId() == 0 && outboundEdge.getV2().getId() == 0)) {

        int outId = outboundEdge.getV2().getId();
        if (outId == 0) {
          outId = graph.vertices().stream().map(Vertex::element)
              .filter(v -> v.equals(outboundEdge.getV2())).findFirst()
              .map(StateNode::getId).orElse(0);
        }

        outboundEdge.getV1().setLabel("(" + currentNode.getId() + "->" + outId + ") ");
        graph.insertEdge(currentNode, outboundEdge.getV2(), outboundEdge.getV1());

        if ((outboundEdge.getV2().isWorking() && currentNode.isWorking())
            || (!outboundEdge.getV2().getOutboundEdges().isEmpty() && outboundEdge.getV2().getOutboundEdges().stream().noneMatch(n -> n.getV1().getValue().contains("µ"))
            && currentNode.isWorking())) {

          var reverseEdge = new StateEdge();
          reverseEdge.setValue(outboundEdge.getV1().getValue().replace('λ', 'µ'));
          reverseEdge.setLabel("(" + outId + "->" + currentNode.getId() + ") ");
          graph.insertEdge(outboundEdge.getV2(), currentNode, reverseEdge);
        }
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
