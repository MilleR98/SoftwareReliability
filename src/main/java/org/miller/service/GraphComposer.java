package org.miller.service;

import groovy.lang.Tuple2;
import java.util.Arrays;
import org.miller.engine.Evaluator;
import org.miller.model.StateEdge;
import org.miller.model.StateNode;

public class GraphComposer {

  private final Evaluator systemReliabilityEvaluator = Evaluator.getInstance();

  public StateNode buildSystemStatesGraph(String elementsSchemaEquation) {

    var elementsCount = Evaluator.findNumberOfElements(elementsSchemaEquation);

    Boolean[] rootState = initRootState(elementsCount);

    return fillNode(rootState, elementsSchemaEquation);
  }

  private Boolean[] initRootState(int elementsCount) {

    var rootState = new Boolean[elementsCount];
    for (int i = 0; i < elementsCount; i++) {

      rootState[i] = true;
    }
    return rootState;
  }

  private StateNode fillNode(Boolean[] state, String elementsSchemaEquation) {

    var stateNode = new StateNode();
    stateNode.setState(state);
    stateNode.setWorking(systemReliabilityEvaluator.evaluateWorkState(elementsSchemaEquation, stateNode.getState()));

    if (stateNode.isWorking()) {
      for (int i = 0; i < state.length; i++) {

        var nextState = Arrays.copyOf(state, state.length);

        if (nextState[i]) {

          nextState[i] = false;

          var childNodePair = new Tuple2<>(new StateEdge(null, "λ[" + i + "]"), fillNode(nextState, elementsSchemaEquation));
          stateNode.getOutboundEdges().add(childNodePair);
        }
      }
    }

    return stateNode;
  }
}
