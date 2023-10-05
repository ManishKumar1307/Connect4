module com.example.connect4 {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.connect4 to javafx.fxml;
    exports com.example.connect4;
}