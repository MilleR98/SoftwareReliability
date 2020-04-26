package org.miller.service;

import flanagan.integration.DerivnFunction;
import flanagan.integration.RungeKutta;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import org.miller.engine.Evaluator;
import org.miller.model.Param;
import org.miller.model.NodeEquation;

public class DifferentialEquationCalculationService {

  private final OrdinaryDefFunction defFunction = new OrdinaryDefFunction();

  public double[][] calculate(double loverBound, double upperBound, double step,
      List<NodeEquation> equations, List<Param> lambdasList, ObservableList<Param> miList) {

    double[] y0 = new double[equations.size() - 3];
    y0[0] = 1;

    RungeKutta rungeKutta = new RungeKutta();

    rungeKutta.setInitialValueOfX(loverBound);
    rungeKutta.setFinalValueOfX(upperBound);
    rungeKutta.setInitialValuesOfY(y0);
    rungeKutta.setStepSize(step);

    var points = new double[(int) ((upperBound - loverBound)/ step)];
    var counter = 0;
    for (double i = loverBound; i < upperBound; i += step) {

      points[counter++] = i;
    }

    defFunction.setLambdas(lambdasList.stream().map(Param::getValue).mapToDouble(Double::doubleValue).toArray());
    defFunction.setMis(miList.stream().map(Param::getValue).mapToDouble(Double::doubleValue).toArray());
    defFunction.setEquations(equations);

    double[][] result = rungeKutta.fourthOrder(defFunction, points);

    double[][] transpose = new double[result[0].length][result.length];

    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[i].length; j++) {
        transpose[j][i] = result[i][j];
      }
    }

    return transpose;
  }

  static class OrdinaryDefFunction implements DerivnFunction {

    private double[] lambdas;
    private double[] mis;
    private String equationsStr;

    public double[] derivn(double x, double[] y) {
      double[] dydx = new double[y.length];

      Evaluator.getInstance().evaluateFunctions(equationsStr, lambdas, mis, dydx, y);

      return dydx;
    }

    public void setLambdas(double[] lambdas) {

      this.lambdas = lambdas;
    }

    public void setEquations(List<NodeEquation> equations) {

      this.equationsStr = equations.stream()
          .takeWhile(eq -> !eq.getEquation().equals(""))
          .map(NodeEquation::getEquation)
          .collect(Collectors.joining(";", "", ";"));
    }

    public void setMis(double[] mis) {
      this.mis = mis;
    }
  }
}
