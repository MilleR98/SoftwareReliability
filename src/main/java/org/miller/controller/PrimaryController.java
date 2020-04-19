package org.miller.controller;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import org.miller.engine.SystemGraphComposer;
import org.miller.model.StateNode;
import org.miller.service.GraphViewService;

public class PrimaryController {

  private final SystemGraphComposer systemGraphComposer = new SystemGraphComposer();
  private final GraphViewService graphViewService = new GraphViewService();
  private final Digraph<StateNode, String> graph = new DigraphEdgeList<>();
  private boolean isGraphInit = false;
  private SmartGraphPanel<StateNode, String> graphView;
  private String elementsSchemaEquation;
  @FXML
  private AnchorPane graphContainer;

  @FXML
  public void initialize() {

    SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
    SmartGraphProperties smartGraphProperties = new SmartGraphProperties(getClass().getClassLoader().getResourceAsStream("smartgraph.properties"));
    graphView = new SmartGraphPanel<>(graph, smartGraphProperties, strategy);
    graphView.setPrefHeight(800);
    graphView.setPrefWidth(2000);
    graphView.getStylesheets().add(getClass().getClassLoader().getResource("smartgraph.css").toExternalForm());
    graphView.setAutomaticLayout(true);

    graphContainer.getChildren().add(graphView);
  }

  @FXML
  public void openAddSchemaModal(ActionEvent actionEvent) {

    TextInputDialog dialog = new TextInputDialog();
    dialog.setWidth(200);
    dialog.setWidth(200);
    dialog.setTitle("New elements schema");
    dialog.setHeaderText("Enter elements equation (if the elements are in parallel - (E1 | E2), if sequentially - (E1 & E2).\n"
        + "For example: (E1 | E2) & E3 & E4 - group of parallel E1 and E2 in sequence with E3 and E4.");

    dialog.showAndWait().ifPresent(name -> this.elementsSchemaEquation = name);

    graphViewService.fillGraphForView(graph, systemGraphComposer.buildSystemStatesGraph(this.elementsSchemaEquation));

    if (!isGraphInit) {

      graphView.init();
      graphView.update();
      isGraphInit = true;
    } else {

      graphView.update();
    }
  }
}
