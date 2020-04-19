package org.miller.controller;

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import org.miller.service.GraphViewService;

public class PrimaryController {

  private final GraphViewService graphViewService = new GraphViewService();
  private String elementsSchemaEquation;
  @FXML
  private AnchorPane graphContainer;

  @FXML
  public void initialize() {

  }

  @FXML
  public void openAddSchemaModal(ActionEvent actionEvent) {

    TextInputDialog dialog = new TextInputDialog();
    dialog.setWidth(200);
    dialog.setWidth(200);
    dialog.setTitle("New elements schema");
    dialog.setHeaderText("Enter elements equation (if the elements are in parallel - (E1 | E2), if sequentially - (E1 & E2).\n"
        + "For example: (E1 | E2) & E3 & E4 - group of parallel E1 and E2 in sequence with E3 and E4.");

    dialog.showAndWait().ifPresent(name -> {
      this.elementsSchemaEquation = name;

      graphContainer.getChildren().clear();

      var graphView = graphViewService.createGraphView(this.elementsSchemaEquation);
      graphView.resize(graphContainer.getPrefWidth(), graphContainer.getPrefHeight());
      graphContainer.getChildren().add(graphView);

      graphView.init();
    });
  }
}
