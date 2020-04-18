package org.miller;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

  private static Scene scene;

  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFXML(fxml));
  }

  private static Parent loadFXML(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    return fxmlLoader.load();
  }

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {

    Digraph<String, String> g = new DigraphEdgeList<>();

    g.insertVertex("1");
    g.insertVertex("2");
    g.insertVertex("3");
    g.insertVertex("4");
    g.insertVertex("5");
    g.insertVertex("6");

    g.insertEdge("1", "2", "λ1");
    g.insertEdge("2", "3", "λ2");
    g.insertEdge("4", "2", "λ3");
    g.insertEdge("2", "1", "λ4");
    g.insertEdge("3", "4", "λ5");
    g.insertEdge("5", "6", "λ6");
    g.insertEdge("6", "1", "λ7");
    g.insertEdge("5", "4", "λ8");
    g.insertEdge("4", "2", "λ10");

//yep, its a loop!
    g.insertEdge("1", "1", "Loop");

    SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
    SmartGraphProperties smartGraphProperties = new SmartGraphProperties(getClass().getClassLoader().getResourceAsStream("smartgraph.properties"));
    SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(g, smartGraphProperties, strategy);
    graphView.getStylesheets().add(this.getClass().getResource("smartgraph.css").toExternalForm());
    Scene graph = new Scene(graphView, 1024, 768);

    scene = new Scene(loadFXML("primary"), 1000, 700);
    stage.setScene(graph);
    stage.show();

    graphView.init();
  }
}
