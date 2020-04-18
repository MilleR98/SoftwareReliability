package org.miller.engine;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemReliabilityEvaluator implements BiFunction<String, Boolean[], Boolean> {

  private static final String ELEMENT_LABEL = "E";
  private static final SystemReliabilityEvaluator INSTANCE = new SystemReliabilityEvaluator();
  private final Binding binding = new Binding();
  private final GroovyShell shellEvaluator = new GroovyShell(binding);

  public static SystemReliabilityEvaluator getInstance() {

    return INSTANCE;
  }

  public static int findNumberOfElements(String elementsSchemaEquation){

    return (int) elementsSchemaEquation.chars().filter(ch -> ch == 'E').count();
  }

  @Override
  public Boolean apply(String elementsSchemaEquation, Boolean[] elementsState) {

    for (int i = 0; i < elementsState.length; i++) {

      elementsSchemaEquation = elementsSchemaEquation.replace(ELEMENT_LABEL + (i + 1), Boolean.toString(elementsState[i]));
    }

    return Boolean.parseBoolean(shellEvaluator.evaluate(elementsSchemaEquation).toString());
  }
}
