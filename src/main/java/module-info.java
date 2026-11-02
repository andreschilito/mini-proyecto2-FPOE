module com.example.miniproyecto2fpoe {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.miniproyecto2fpoe.controller to javafx.fxml;

    exports com.example.miniproyecto2fpoe;
    exports com.example.miniproyecto2fpoe.view;
}