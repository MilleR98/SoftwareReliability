package org.miller.engine;

import groovy.lang.Tuple2;
import java.util.Arrays;
import org.miller.model.StateNode;

public class SystemGraphComposer {

  private final SystemReliabilityEvaluator systemReliabilityEvaluator = SystemReliabilityEvaluator.getInstance();

  public StateNode buildSystemStatesGraph(String elementsSchemaEquation) {

    var elementsCount = SystemReliabilityEvaluator.findNumberOfElements(elementsSchemaEquation);

    var rootState = new Boolean[elementsCount];
    for (int i = 0; i < elementsCount; i++) {

      rootState[i] = true;
    }

    return fillNode(rootState, elementsSchemaEquation);
  }

  private StateNode fillNode(Boolean[] state, String elementsSchemaEquation) {

    var stateNode = new StateNode();
    stateNode.setState(state);
    stateNode.setWorking(systemReliabilityEvaluator.apply(elementsSchemaEquation, stateNode.getState()));

    if (stateNode.isWorking()) {
      for (int i = 0; i < state.length; i++) {

        var nextState = Arrays.copyOf(state, state.length);

        if (nextState[i]) {

          nextState[i] = false;

          var childNodePair = new Tuple2<>("λ" + (i + 1), fillNode(nextState, elementsSchemaEquation));
          stateNode.getOutcomingEdges().add(childNodePair);
        }
      }
    }

    return stateNode;
  }
}
