package org.miller.model;

import com.brunomnsilva.smartgraph.graph.Vertex;
import groovy.lang.Tuple2;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StateNode implements Vertex<StateNode> {

  private int id;
  private boolean isWorking;
  @EqualsAndHashCode.Include
  private Boolean[] state;
  @EqualsAndHashCode.Include
  private boolean isNodeWithRepair;
  private boolean isNodeWithEdgeToRepaired;
  private int repairedIndex = -1;
  private Set<Tuple2<StateEdge, StateNode>> outboundEdges = new HashSet<>();
  private String stateEquation;

  public static String binaryState(Boolean[] targetState, boolean isNodeWithRepair, int repairedIndex) {

    var binaryStateString = new StringBuilder();
    binaryStateString.append("(");

    for (int i = 0; i < targetState.length; i++) {
      var stateItem = targetState[i];

      binaryStateString.append(stateItem ? "1" : "0");

      if (isNodeWithRepair && repairedIndex == i) {

        binaryStateString.append("^");
      }
    }

    binaryStateString.append(")");

    return binaryStateString.toString();
  }

  public String binaryState(){

    return binaryState(state, isNodeWithRepair, repairedIndex);
  }

  @Override
  public String toString() {

    return id + ", " + binaryState(state, isNodeWithRepair, repairedIndex);
  }

  @Override
  public StateNode element() {

    return this;
  }
}
