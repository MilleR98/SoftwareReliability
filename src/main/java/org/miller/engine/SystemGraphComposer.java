package org.miller.engine;

import org.miller.model.StateNode;

public class SystemGraphComposer {

  private final SystemReliabilityEvaluator systemReliabilityEvaluator = SystemReliabilityEvaluator.getInstance();

  public StateNode buildSystemStatesGraph(String elementsSchemaEquation) {

    var elementsCount = SystemReliabilityEvaluator.findNumberOfElements(elementsSchemaEquation);

    var rootNode = new StateNode();
    rootNode.setId(1);

    var rootState = new Boolean[elementsCount];
    for (int i = 0; i < elementsCount; i++) {

      rootState[i] = true;
    }

    rootNode.setState(rootState);
    rootNode.setWorking(systemReliabilityEvaluator.apply(elementsSchemaEquation, rootNode.getState()));

    return rootNode;
  }
}
