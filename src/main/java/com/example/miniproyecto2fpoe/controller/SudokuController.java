package com.example.miniproyecto2fpoe.controller;

import com.example.miniproyecto2fpoe.model.SudokuBoard;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.example.miniproyecto2fpoe.model.HintProvider;

import java.util.OptionalInt;

/**
 * Connects the Sudoku board model to the graphical interface.
 */
public class SudokuController {

    private final SudokuBoard board = new SudokuBoard();
    private final HintProvider hintProvider = board;

    private final Label[][] cells =
            new Label[SudokuBoard.SIZE][SudokuBoard.SIZE];

    private int selectedRow = -1;
    private int selectedColumn = -1;
    private boolean gameStarted = false;

    @FXML
    private GridPane boardGrid;

    @FXML
    private Label conflictMessage;

    @FXML
    private Label hintsLabel;

    @FXML
    private Button hintButton;

    @FXML
    private Label hintMessage;

    /**
     * Creates the cells and displays the initial board.
     */
    @FXML
    private void initialize() {
        boardGrid.setFocusTraversable(true);
        boardGrid.setOnKeyPressed(event -> onBoardKeyPressed(event));
        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int column = 0; column < SudokuBoard.SIZE; column++) {
                Label cell = new Label();

                cell.setAlignment(Pos.CENTER);
                cell.setMinSize(42, 42);
                cell.setPrefSize(42, 42);

                final int cellRow = row;
                final int cellColumn = column;

                cell.setOnMouseClicked(event -> selectCell(cellRow, cellColumn));

                cells[row][column] = cell;
                boardGrid.add(cell, column, row);
            }
        }

        refreshBoard();
    }
    /**
     * Selects a cell and highlights it.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     */
    private void selectCell(int row, int column) {
        selectedRow = row;
        selectedColumn = column;
        hintMessage.setText("");

        refreshBoard();
        boardGrid.requestFocus();
    }

    /**
     * Removes the current cell selection.
     */
    private void clearSelection() {
        selectedRow = -1;
        selectedColumn = -1;
    }
    /**
     * Handles number entry and deletion in the selected editable cell.
     *
     * @param event the keyboard event
     */
    private void onBoardKeyPressed(KeyEvent event) {
        if (!gameStarted || selectedRow == -1) {
            return;
        }

        if (board.isFixed(selectedRow, selectedColumn)) {
            return;
        }

        if (event.getCode() == KeyCode.BACK_SPACE
                || event.getCode() == KeyCode.DELETE) {
            board.clearCell(selectedRow, selectedColumn);
        } else {
            int value = switch (event.getCode()) {
                case DIGIT1, NUMPAD1 -> 1;
                case DIGIT2, NUMPAD2 -> 2;
                case DIGIT3, NUMPAD3 -> 3;
                case DIGIT4, NUMPAD4 -> 4;
                case DIGIT5, NUMPAD5 -> 5;
                case DIGIT6, NUMPAD6 -> 6;
                default -> 0;
            };

            if (value == 0) {
                return;
            }

            board.setValue(selectedRow, selectedColumn, value);
        }
        hintMessage.setText("");
        refreshBoard();
        event.consume();
        checkVictory();
    }
    /**
     * Ends the active game and notifies the player when the board is solved.
     */
    private void checkVictory() {
        if (!gameStarted || !board.isSolved()) {
            return;
        }

        gameStarted = false;
        refreshBoard();

        Alert victory = new Alert(Alert.AlertType.INFORMATION);
        victory.initOwner(boardGrid.getScene().getWindow());
        victory.setTitle("Sudoku completado");
        victory.setHeaderText("¡Felicitaciones!");
        victory.setContentText("Completaste el tablero correctamente.");

        ButtonType acceptButton =
                new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        victory.getButtonTypes().setAll(acceptButton);

        victory.showAndWait();
    }

    /**
     * Updates cell texts, selection, and conflict indicators.
     */
    private void refreshBoard() {
        boolean conflictFound = false;

        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int column = 0; column < SudokuBoard.SIZE; column++) {
                int value = board.getValue(row, column);

                if (value == 0) {
                    cells[row][column].setText("");
                } else {
                    cells[row][column].setText(Integer.toString(value));
                }

                String style =
                        "-fx-border-width: 2; -fx-border-color: transparent;";

                if (row == selectedRow && column == selectedColumn) {
                    style += "-fx-background-color: lightblue;";
                }

                if (board.hasConflict(row, column)) {
                    style += "-fx-border-color: red;";
                    conflictFound = true;
                }

                cells[row][column].setStyle(style);
            }
        }

        if (conflictFound) {
            conflictMessage.setText("Hay números repetidos.");
        } else {
            conflictMessage.setText("");
        }
        hintsLabel.setText("Ayudas restantes: " + hintProvider.getRemainingHints());

        hintButton.setDisable(
                !gameStarted
                        || selectedRow == -1
                        || !hintProvider.canRequestHint()
                        || conflictFound
                        || board.getValue(selectedRow, selectedColumn) != 0
        );
    }
    /**
     * Displays a suggestion without changing the board values.
     */
    @FXML
    private void onHintClick() {
        if (!gameStarted || selectedRow == -1) {
            return;
        }

        OptionalInt hint =
                hintProvider.requestHint(selectedRow, selectedColumn);

        if (hint.isPresent()) {
            hintMessage.setText("Número sugerido: " + hint.getAsInt());
        } else {
            hintMessage.setText("No hay una sugerencia disponible para este tablero.");
        }

        refreshBoard();
        boardGrid.requestFocus();
    }

    /**
     * Starts a new game only after the user confirms the action.
     */
    @FXML
    private void onNewGameClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.initOwner(boardGrid.getScene().getWindow());
        confirmation.setTitle("Nueva partida");
        confirmation.setHeaderText("¿Quieres iniciar una nueva partida?");
        confirmation.setContentText("Se reemplazará el tablero actual.");

        ButtonType acceptButton =
                new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton =
                new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmation.getButtonTypes().setAll(acceptButton, cancelButton);

        if (confirmation.showAndWait().orElse(cancelButton) == acceptButton) {
            clearSelection();
            board.startNewGame();
            gameStarted = true;
            hintMessage.setText("");
            refreshBoard();
        }
    }
}
