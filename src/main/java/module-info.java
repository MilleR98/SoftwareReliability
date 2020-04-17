module org.miller {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.codehaus.groovy;

    opens org.miller to javafx.fxml;
    exports org.miller;
}
