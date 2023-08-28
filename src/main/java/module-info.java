module com.example.timberman_maven {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.timberman_maven to javafx.fxml;
    exports com.example.timberman_maven;
}