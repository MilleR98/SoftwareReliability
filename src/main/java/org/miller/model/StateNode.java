package org.miller.model;

import groovy.lang.Tuple2;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"incomingEdges", "outcomingEdges", "stateEquation"})
public class StateNode {

  private int id;
  private boolean isWorking;
  @EqualsAndHashCode.Include
  private Boolean[] state;
  private Set<Tuple2<String, StateNode>> incomingEdges = new HashSet<>();
  private Set<Tuple2<String, StateNode>> outcomingEdges = new HashSet<>();
  private String stateEquation;
}
