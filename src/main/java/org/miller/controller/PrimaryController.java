package org.miller.controller;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.miller.model.NodeEquation;
import org.miller.model.StateEdge;
import org.miller.model.StateNode;
import org.miller.service.GraphViewService;

public class PrimaryController {

  private final GraphViewService graphViewService = new GraphViewService();
  private DigraphEdgeList<StateNode, StateEdge> digraph;
  private String elementsSchemaEquation;
  @FXML
  private AnchorPane graphContainer;
  @FXML
  private TableView<NodeEquation> equationsTable;
  @FXML
  private TableColumn<NodeEquation, String> equationColumn;
  @FXML
  private TableColumn<NodeEquation, String> statusColumn;
  @FXML
  private TableColumn<NodeEquation, Integer> idColumn;

  @FXML
  public void initialize() {

    equationColumn.setCellValueFactory(new PropertyValueFactory<>("equation"));
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    equationColumn.setSortable(false);
    statusColumn.setSortable(false);
    idColumn.setSortable(false);
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

      graphContainer.getChildren().clear();

      var graphViewPair = graphViewService.createGraphView(this.elementsSchemaEquation);
      var graphPanel = graphViewPair.getV1();
      digraph = graphViewPair.getV2();
      graphPanel.resize(graphContainer.getPrefWidth(), graphContainer.getPrefHeight());
      graphContainer.getChildren().add(graphPanel);

      graphPanel.init();

      equationsTable.setItems(FXCollections.observableArrayList(graphViewService.getNodeEquations(digraph)));
    });
  }
}
