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
  private Set<Tuple2<StateEdge, StateNode>> outboundEdges = new HashSet<>();
  private String stateEquation;

  @Override
  public String toString() {

    return id + ", " + binaryState();
  }

  private String binaryState() {

    StringBuilder strBuilder = new StringBuilder();
    strBuilder.append("(");

    for (var stateItem : this.state) {

      strBuilder.append(stateItem ? "1" : "0");
    }

    strBuilder.append(")");

    return strBuilder.toString();
  }

  @Override
  public StateNode element() {

    return this;
  }
}
