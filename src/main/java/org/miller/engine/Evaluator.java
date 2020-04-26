package org.miller.engine;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Evaluator {

  private static final String ELEMENT_LABEL = "E";
  private static final Evaluator INSTANCE = new Evaluator();
  private final Binding binding = new Binding();
  private final GroovyShell shellEvaluator = new GroovyShell(binding);

  public static Evaluator getInstance() {

    return INSTANCE;
  }

  public static int findNumberOfElements(String elementsSchemaEquation) {

    return (int) elementsSchemaEquation.chars().filter(ch -> ch == 'E').count();
  }

  public boolean evaluateWorkState(String elementsSchemaEquation, Boolean[] elementsState) {

    for (int i = 0; i < elementsState.length; i++) {

      elementsSchemaEquation = elementsSchemaEquation.replace(ELEMENT_LABEL + (i + 1), Boolean.toString(elementsState[i]));
    }

    return Boolean.parseBoolean(shellEvaluator.evaluate(elementsSchemaEquation).toString());
  }

  public void evaluateFunctions(String function, double[] lambdas, double[] mis, double[] dxdy, double[] y) {

    binding.setVariable("λ", lambdas);
    binding.setVariable("μ", mis);
    binding.setVariable("dP", dxdy);
    binding.setVariable("P", y);

    String pureExpression = function.replace("(t)", "").replace("/dt", "");

    shellEvaluator.evaluate(pureExpression);
  }
}
