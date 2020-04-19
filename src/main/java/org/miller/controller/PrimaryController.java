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

public class PrimaryController {

  private boolean isGraphInit = false;
  private SmartGraphPanel<String, String> graphView;
  private SystemGraphComposer systemGraphComposer = new SystemGraphComposer();
  private Digraph<String, String> graph = new DigraphEdgeList<>();
  private String elementsSchemaEquation;
  @FXML
  private AnchorPane graphContainer;

  @FXML
  public void initialize() {

    SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
    SmartGraphProperties smartGraphProperties = new SmartGraphProperties(getClass().getClassLoader().getResourceAsStream("smartgraph.properties"));
    graphView = new SmartGraphPanel<>(graph, smartGraphProperties, strategy);

    graphView.setPrefHeight(600);
    graphView.setPrefWidth(1000);
    graphView.getStylesheets().add(getClass().getClassLoader().getResource("smartgraph.css").toExternalForm());

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

    var rootNode = systemGraphComposer.buildSystemStatesGraph(this.elementsSchemaEquation);

   /* graph.vertices().forEach(graph::removeVertex);
    graph.edges().forEach(graph::removeEdge);

    graph.insertVertex("1");
    graph.insertVertex("2");
    graph.insertVertex("3");
    graph.insertVertex("4");
    graph.insertVertex("5");
    graph.insertVertex("6");
    graph.insertVertex("7");

    graph.insertEdge("1", "2", "λ1");
    graph.insertEdge("1", "3", "λ2");
    graph.insertEdge("1", "4", "λ3");

    graph.insertEdge("3", "5", "λ4");
    graph.insertEdge("3", "6", "λ5");

    graph.insertEdge("4", "7", "λ6");

    if(!isGraphInit){

      graphView.init();
      graphView.update();
      isGraphInit = true;
    }else {

      graphView.update();
    }*/
  }
}
