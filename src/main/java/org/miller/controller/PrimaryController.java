package org.miller.controller;

import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import groovy.lang.Tuple2;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
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
  public void initialize() {

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
    });
  }
}
