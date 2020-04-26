package org.miller.service;

import groovy.lang.Tuple2;
import java.util.Arrays;
import org.miller.engine.Evaluator;
import org.miller.model.StateEdge;
import org.miller.model.StateNode;

public class GraphComposer {

  private static int indexWithOneRepair;
  private final Evaluator systemReliabilityEvaluator = Evaluator.getInstance();

  public StateNode buildSystemStatesGraph(String elementsSchemaEquation, int indexOfOneTimeRepairElement) {

    var elementsCount = Evaluator.findNumberOfElements(elementsSchemaEquation);
    indexWithOneRepair = indexOfOneTimeRepairElement - 1;

    Boolean[] rootState = initRootState(elementsCount);

    return fillNode(rootState, elementsSchemaEquation, false);
  }

  private Boolean[] initRootState(int elementsCount) {

    var rootState = new Boolean[elementsCount];
    for (int i = 0; i < elementsCount; i++) {

      rootState[i] = true;
    }
    return rootState;
  }

  private StateNode fillNode(Boolean[] currentState, String elementsSchemaEquation, boolean isRepaired) {

    var stateNode = new StateNode();
    stateNode.setState(currentState);
    stateNode.setRepairedIndex(indexWithOneRepair);
    stateNode.setNodeWithRepair(isRepaired);
    stateNode.setWorking(systemReliabilityEvaluator.evaluateWorkState(elementsSchemaEquation, stateNode.getState()));

    if (stateNode.isWorking()) {

      addChildNodes(currentState, elementsSchemaEquation, stateNode);
    } else {

      if (!stateNode.getState()[indexWithOneRepair] && !stateNode.isNodeWithRepair()) {

        stateNode.setNodeWithEdgeToRepaired(true);

        var repairedState = Arrays.copyOf(currentState, currentState.length);
        repairedState[indexWithOneRepair] = true;

        var repairedStateNode = new StateNode();
        repairedStateNode.setState(repairedState);
        repairedStateNode.setRepairedIndex(indexWithOneRepair);
        repairedStateNode.setNodeWithRepair(true);
        repairedStateNode.setWorking(systemReliabilityEvaluator.evaluateWorkState(elementsSchemaEquation, repairedStateNode.getState()));

        addChildNodes(repairedState, elementsSchemaEquation, repairedStateNode);

        var childNodePair = new Tuple2<>(new StateEdge(null, "µ[" + indexWithOneRepair + "]"), repairedStateNode);
        stateNode.getOutboundEdges().add(childNodePair);
      }else if(stateNode.getState()[indexWithOneRepair]){

        addChildNodes(currentState, elementsSchemaEquation, stateNode);
      }
    }

    return stateNode;
  }

  private void addChildNodes(Boolean[] state, String elementsSchemaEquation, StateNode stateNode) {
    for (int i = 0; i < state.length; i++) {

      var nextState = Arrays.copyOf(state, state.length);

      if (nextState[i]) {

        nextState[i] = false;

        var childNodePair = new Tuple2<>(new StateEdge(null, "λ[" + i + "]"), fillNode(nextState, elementsSchemaEquation, stateNode.isNodeWithRepair()));
        stateNode.getOutboundEdges().add(childNodePair);
      }
    }
  }
}
