module com.example.skycontrol {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.skycontrol to javafx.fxml;
    exports com.example.skycontrol;
}