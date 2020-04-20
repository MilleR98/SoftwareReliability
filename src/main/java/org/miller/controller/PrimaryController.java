package org.miller.controller;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.DoubleStringConverter;
import org.miller.engine.SystemReliabilityEvaluator;
import org.miller.model.Lambda;
import org.miller.model.NodeEquation;
import org.miller.model.StateEdge;
import org.miller.model.StateNode;
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
  private DigraphEdgeList<StateNode, StateEdge> digraph;
  private String elementsSchemaEquation;
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
  }

  @FXML
  public void openAddSchemaModal(ActionEvent actionEvent) {

    TextInputDialog dialog = new TextInputDialog();
    dialog.setWidth(200);
    dialog.setWidth(150);
    dialog.setTitle("New elements schema");
    dialog.setHeaderText("Enter elements equation (if the elements are in parallel - (E1 | E2), if sequentially - (E1 & E2).\n"
        + "For example: (E1 | E2) & E3 & E4 - group of parallel E1 and E2 in sequence with E3 and E4.");

    dialog.showAndWait().ifPresent(name -> {
      this.elementsSchemaEquation = name;
      this.numberOfElements = SystemReliabilityEvaluator.findNumberOfElements(this.elementsSchemaEquation);
      graphContainer.getChildren().clear();

      initGraphView();

      equationsTable.setItems(FXCollections.observableArrayList(graphViewService.getNodeEquations(digraph)));

      initLambdaValuesTable();

      initCalculationTable();
    });
  }

  private void initGraphView() {

    var graphViewPair = graphViewService.createGraphView(this.elementsSchemaEquation);
    var graphPanel = graphViewPair.getV1();
    digraph = graphViewPair.getV2();
    graphPanel.resize(graphContainer.getPrefWidth(), graphContainer.getPrefHeight());
    graphContainer.getChildren().add(graphPanel);
    graphPanel.init();
  }

  private void initLambdaValuesTable() {

    for (int i = 1; i <= this.numberOfElements; i++) {

      lambdasList.add(new Lambda("λ" + i, DEFAULT_LAMBDA_VALUES.getOrDefault("λ" + i, 0d)));
    }
  }

  private void initCalculationTable() {

    calculationsTable.getColumns().removeIf(col -> Objects.isNull(col.getId()));
    tColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
    calculationsTable.setEditable(false);
    for (int i = 1; i <= digraph.vertices().size(); i++) {

      final int finalIdx = i;
      TableColumn<ObservableList<Double>, Double> column = new TableColumn<>("P" + i);
      column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(finalIdx)));
      column.setEditable(false);
      column.setSortable(false);
      calculationsTable.getColumns().add(column);
    }
  }

  @FXML
  public void calculateEquations(ActionEvent actionEvent) {

  }
}
