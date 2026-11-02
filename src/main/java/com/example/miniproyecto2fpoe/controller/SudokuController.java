package com.example.miniproyecto2fpoe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SudokuController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
