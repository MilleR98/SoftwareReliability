package org.miller.engine;

import groovy.lang.Tuple2;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.miller.model.StateNode;

public class SystemGraphComposer {

  private final Set<StateNode> allNodes = new HashSet<>();
  private final SystemReliabilityEvaluator systemReliabilityEvaluator = SystemReliabilityEvaluator.getInstance();

  public StateNode buildSystemStatesGraph(String elementsSchemaEquation) {

    var elementsCount = SystemReliabilityEvaluator.findNumberOfElements(elementsSchemaEquation);

    var rootState = new Boolean[elementsCount];
    for (int i = 0; i < elementsCount; i++) {

      rootState[i] = true;
    }

    Integer counter = 1;
    StateNode rootNode = fillNode(rootState, counter, elementsSchemaEquation);

    updateIds(Set.of(rootNode), 1);

    allNodes.clear();

    return rootNode;
  }

  private void updateIds(Set<StateNode> nodes, Integer counter) {

    for (StateNode stateNode : nodes) {
      stateNode.setId(counter);
      counter += 1;
      System.out.print(stateNode + ", ");
    }

    System.out.println();

    for (StateNode n : nodes) {
      updateIds(n.getOutcomingEdges().stream().map(Tuple2::getV2).collect(Collectors.toSet()), counter);
      counter += 1;
    }
  }

  private StateNode fillNode(Boolean[] state, Integer counter, String elementsSchemaEquation) {

    var stateNode = new StateNode();
    stateNode.setId(counter);
    stateNode.setState(state);
    stateNode.setWorking(systemReliabilityEvaluator.apply(elementsSchemaEquation, stateNode.getState()));

    if (stateNode.isWorking()) {
      for (int i = 0; i < state.length; i++) {

        var nextState = Arrays.copyOf(state, state.length);

        if (nextState[i]) {

          nextState[i] = false;
          counter = counter + 1;

          var childNodePair = new Tuple2<>("λ", fillNode(nextState, counter, elementsSchemaEquation));
          if (!allNodes.contains(childNodePair.getV2())) {

            allNodes.add(childNodePair.getV2());
            stateNode.getOutcomingEdges().add(childNodePair);
          } else {

            counter = counter + 1;
          }
        } else {

          counter = counter + 1;
        }
      }
    }

    return stateNode;
  }
}
