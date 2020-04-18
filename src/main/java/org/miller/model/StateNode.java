package org.miller.model;

import groovy.lang.Tuple2;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateNode {

  private int id;
  private boolean isWorking;
  private Boolean[] state;
  private List<Tuple2<Integer, StateNode>> incomingEdges = new ArrayList<>();
  private List<Tuple2<Integer, StateNode>> outcomingEdges = new ArrayList<>();
  private String stateEquation;
}
