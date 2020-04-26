package org.miller.controller;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.DoubleStringConverter;
import org.miller.engine.Evaluator;
import org.miller.model.Lambda;
import org.miller.model.NodeEquation;
import org.miller.model.StateEdge;
import org.miller.model.StateNode;
import org.miller.service.DifferentialEquationCalculationService;
import org.miller.service.EquationsBuilderService;
import org.miller.service.GraphViewService;

public class PrimaryController {

  private static final Map<String, Double> DEFAULT_LAMBDA_VALUES = Map.of(
      "λ1", 0.0005d,
      "λ2", 0.0004d,
      "λ3", 0.0003d,
      "λ4", 0.0002d,
      "λ5", 0.0001d
  );

  private final ObservableList<Lambda> lambdasList = FXCollections.observableArrayList();
  private final GraphViewService graphViewService = new GraphViewService();
  private final EquationsBuilderService equationsBuilderService = new EquationsBuilderService();
  private final DifferentialEquationCalculationService calculationService = new DifferentialEquationCalculationService();
  private final List<Integer> workingColumnIndexes = new ArrayList<>();
  private DigraphEdgeList<StateNode, StateEdge> digraph;
  private int calculationsCounter = 1;

  private int numberOfElements;
  //Graph View Tab
  @FXML
  private AnchorPane graphContainer;
  //Equations System Tab
  @FXML
  private TableView<NodeEquation> equationsTable;
  @FXML
  private TableColumn<NodeEquation, String> equationColumn;
  @FXML
  private TableColumn<NodeEquation, String> statusColumn;
  @FXML
  private TableColumn<NodeEquation, Integer> idColumn;
  //Calculations Tab
  @FXML
  private TableView<Lambda> lambdaTable;
  @FXML
  private TableColumn<Lambda, String> lambdaLabelColumn;
  @FXML
  private TableColumn<Lambda, Double> lambdaValueColumn;
  @FXML
  private TableView<ObservableList<Double>> calculationsTable;
  @FXML
  private TableColumn<ObservableList<Double>, Double> tColumn;
  @FXML
  private TextField loverBoundInput;
  @FXML
  private TextField upperBoundInput;
  @FXML
  private TextField stepInput;
  //Chart tab
  @FXML
  private LineChart<Number, Number> probabilityChart;
  @FXML
  private NumberAxis xAxis;
  @FXML
  private NumberAxis yAxis;

  @FXML
  public void initialize() {

    //Equations System Tab
    equationColumn.setCellValueFactory(new PropertyValueFactory<>("equation"));
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    equationColumn.setSortable(false);
    statusColumn.setSortable(false);
    idColumn.setSortable(false);

    //Calculations Tab
    lambdaTable.setEditable(true);
    lambdaValueColumn.setEditable(true);
    lambdaValueColumn.setOnEditCommit(t -> t.getRowValue().setValue(t.getNewValue()));
    lambdaLabelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
    lambdaValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
    lambdaValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
    lambdaValueColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue(t.getNewValue()));

    lambdaTable.setItems(lambdasList);

    initChart();
  }

  @FXML
  public void openAddSchemaModal(ActionEvent actionEvent) {

    TextInputDialog dialog = new TextInputDialog();
    dialog.setWidth(200);
    dialog.setWidth(150);
    dialog.setTitle("New elements schema");
    dialog.setHeaderText("Enter elements equation (if the elements are in parallel - (E1 | E2), if sequentially - (E1 & E2).\n"
        + "For example: (E1 | E2) & E3 & E4 - group of parallel E1 and E2 in sequence with E3 and E4.");

    dialog.showAndWait().ifPresent(elementsSchemaEquation -> {

      this.numberOfElements = Evaluator.findNumberOfElements(elementsSchemaEquation);
      graphContainer.getChildren().clear();

      initGraphView(elementsSchemaEquation);

      equationsTable.setItems(FXCollections.observableArrayList(equationsBuilderService.getNodeEquations(digraph)));

      initLambdaValuesTable();

      initCalculationTable();
    });
  }

  private void initGraphView(String elementsSchemaEquation) {

    var graphViewPair = graphViewService.createGraphView(elementsSchemaEquation);
    var graphPanel = graphViewPair.getFirst();
    digraph = graphViewPair.getSecond();
    graphPanel.resize(graphContainer.getPrefWidth(), graphContainer.getPrefHeight());
    graphContainer.getChildren().add(graphPanel);
    graphPanel.init();
  }

  private void initLambdaValuesTable() {

    lambdasList.clear();

    for (int i = 1; i <= this.numberOfElements; i++) {

      lambdasList.add(new Lambda("λ" + i, DEFAULT_LAMBDA_VALUES.getOrDefault("λ" + i, 0d)));
    }
  }

  private void initCalculationTable() {

    calculationsTable.getColumns().removeIf(col -> Objects.isNull(col.getId()));
    tColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
    calculationsTable.setEditable(false);
    workingColumnIndexes.clear();
    calculationsTable.getItems().clear();

    for (var vertex : digraph.vertices().stream().sorted(Comparator.comparingInt(v -> v.element().getId())).collect(Collectors.toList())) {

      TableColumn<ObservableList<Double>, Double> column = new TableColumn<>("P" + vertex.element().getId());
      column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(vertex.element().getId() + 1)));
      column.setEditable(false);
      column.setSortable(false);
      column.setStyle(vertex.element().isWorking() ? "-fx-background-color: #ebffec; -fx-text-fill: black;" : "-fx-background-color: #ffebeb;-fx-text-fill: black;");
      calculationsTable.getColumns().add(column);
      if (vertex.element().isWorking()) {

        workingColumnIndexes.add(vertex.element().getId() + 1);
      }
    }

    TableColumn<ObservableList<Double>, Double> column = new TableColumn<>("Failure-free Probability");
    column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(calculationsTable.getColumns().size() - 1)));
    column.setEditable(false);
    column.setSortable(false);
    calculationsTable.getColumns().add(column);
  }

  @FXML
  public void calculateEquations(ActionEvent actionEvent) {

    double loverBound = Double.parseDouble(loverBoundInput.getText());
    double upperBound = Double.parseDouble(upperBoundInput.getText());
    double step = Double.parseDouble(stepInput.getText());

    double[][] result = calculationService.calculate(loverBound, upperBound, step, equationsTable.getItems(), lambdasList);

    calculationsTable.getItems().clear();

    XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();
    dataSeries.setName("P(t)" + calculationsCounter++);

    for (double[] resultRow : result) {

      List<Double> values = DoubleStream.of(resultRow).boxed().collect(Collectors.toList());

      double totalWorkingPValue = 0;
      for (var indexOfWorking : workingColumnIndexes) {

        totalWorkingPValue += values.get(indexOfWorking);
      }

      values.add(totalWorkingPValue);

      calculationsTable.getItems().add(FXCollections.observableList(values));

      dataSeries.getData().add(new XYChart.Data<>(resultRow[0], totalWorkingPValue));
    }

    probabilityChart.getData().add(dataSeries);
  }

  private void initChart() {

    xAxis.setLabel("t");
    yAxis.setLabel("P(t)");
    probabilityChart.setTitle("Probability of failure-free operation");
    probabilityChart.setCreateSymbols(false);
  }

  @FXML
  public void clearChartData(ActionEvent actionEvent){

    calculationsCounter = 1;
    probabilityChart.getData().clear();
  }
}
