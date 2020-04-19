module org.miller {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.codehaus.groovy;
    requires JavaFXSmartGraph;
    requires static lombok;

    opens org.miller.controller to javafx.fxml;
    opens org.miller.model to javafx.base;
    exports org.miller;
}
