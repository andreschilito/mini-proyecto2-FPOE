module com.example.miniproyecto2fpoe {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.miniproyecto2fpoe to javafx.fxml;
    exports com.example.miniproyecto2fpoe;
}