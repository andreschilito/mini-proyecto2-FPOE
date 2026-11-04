package com.example.miniproyecto2fpoe.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SudokuApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SudokuApplication.class.getResource("/com/example/miniproyecto2fpoe/sudoku-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 560);
        stage.setTitle("Sudoku");
        stage.setScene(scene);
        stage.show();
    }
}
