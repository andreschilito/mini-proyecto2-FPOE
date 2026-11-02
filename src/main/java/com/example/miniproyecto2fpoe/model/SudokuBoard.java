package com.example.miniproyecto2fpoe.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.OptionalInt;

/**
 * Stores the values and fixed clues of a 6x6 Sudoku board.
 */
public class SudokuBoard {

    public static final int SIZE = 6;
    public static final int MAX_HINTS = 3;

    private final int[][] values;
    private final boolean[][] fixedCells;
    private int remainingHints;

    /**
     * Creates an empty Sudoku board.
     */
    public SudokuBoard() {
        values = new int[SIZE][SIZE];
        fixedCells = new boolean[SIZE][SIZE];
    }

    /**
     * Starts a new game with two fixed clues per 2x3 block.
     * Clues are selected from a valid completed board.
     */
    public void startNewGame() {
        int[][] solution = {
                {1, 2, 3, 4, 5, 6},
                {4, 5, 6, 1, 2, 3},
                {2, 3, 4, 5, 6, 1},
                {5, 6, 1, 2, 3, 4},
                {3, 4, 5, 6, 1, 2},
                {6, 1, 2, 3, 4, 5}
        };

        ArrayList<Integer> shuffledNumbers = new ArrayList<>();

        for (int number = 1; number <= SIZE; number++) {
            shuffledNumbers.add(number);
        }

        Collections.shuffle(shuffledNumbers);

        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                int originalValue = solution[row][column];
                solution[row][column] = shuffledNumbers.get(originalValue - 1);
            }
        }


        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                values[row][column] = 0;
                fixedCells[row][column] = false;
            }
        }

        for (int blockRow = 0; blockRow < SIZE; blockRow += 2) {
            for (int blockColumn = 0;
                 blockColumn < SIZE;
                 blockColumn += 3) {

                ArrayList<Integer> positions = new ArrayList<>();

                for (int position = 0; position < 6; position++) {
                    positions.add(position);
                }

                Collections.shuffle(positions);

                for (int clue = 0; clue < 2; clue++) {
                    int position = positions.get(clue);

                    int row = blockRow + position / 3;
                    int column = blockColumn + position % 3;

                    values[row][column] = solution[row][column];
                    fixedCells[row][column] = true;

                }
            }
        }
        remainingHints = MAX_HINTS;
    }

    /**
     * Returns the value at the specified position.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @return the cell value, or zero if the cell is empty
     */
    public int getValue(int row, int column) {
        validatePosition(row, column);
        return values[row][column];
    }

    /**
     * Indicates whether a cell contains a fixed clue.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @return true if the cell contains a fixed clue
     */
    public boolean isFixed(int row, int column) {
        validatePosition(row, column);
        return fixedCells[row][column];
    }

    /**
     * Sets the value of an editable cell.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @param value the number to enter, from 1 to 6
     * @throws IndexOutOfBoundsException if the position is outside the board
     * @throws IllegalArgumentException if the value is outside the allowed range
     * @throws IllegalStateException if the cell contains a fixed clue
     */
    public void setValue(int row, int column, int value) {
        validatePosition(row, column);

        if (value < 1 || value > SIZE) {
            throw new IllegalArgumentException(
                    "Value must be between 1 and " + SIZE
            );
        }

        if (fixedCells[row][column]) {
            throw new IllegalStateException("Fixed clues cannot be modified");
        }

        values[row][column] = value;
    }

    /**
     * Clears an editable cell.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @throws IndexOutOfBoundsException if the position is outside the board
     * @throws IllegalStateException if the cell contains a fixed clue
     */
    public void clearCell(int row, int column) {
        validatePosition(row, column);

        if (fixedCells[row][column]) {
            throw new IllegalStateException("Fixed clues cannot be cleared");
        }

        values[row][column] = 0;
    }

    /**
     * Checks whether a value can be placed at the specified position.
     * The current value of the target cell is ignored.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @param value the number to check, from 1 to 6
     * @return true if no other cell in the row, column, or block has the value
     * @throws IndexOutOfBoundsException if the position is outside the board
     * @throws IllegalArgumentException if the value is outside the allowed range
     */
    public boolean isValidMove(int row, int column, int value) {
        validatePosition(row, column);

        if (value < 1 || value > SIZE) {
            throw new IllegalArgumentException(
                    "Value must be between 1 and " + SIZE
            );
        }

        for (int index = 0; index < SIZE; index++) {
            if (index != column && values[row][index] == value) {
                return false;
            }

            if (index != row && values[index][column] == value) {
                return false;
            }
        }

        int blockStartRow = (row / 2) * 2;
        int blockStartColumn = (column / 3) * 3;

        for (int currentRow = blockStartRow;
             currentRow < blockStartRow + 2;
             currentRow++) {

            for (int currentColumn = blockStartColumn;
                 currentColumn < blockStartColumn + 3;
                 currentColumn++) {

                if (currentRow == row && currentColumn == column) {
                    continue;
                }

                if (values[currentRow][currentColumn] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Indicates whether a filled cell violates the Sudoku rules.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @return true if the cell conflicts with another cell
     * @throws IndexOutOfBoundsException if the position is outside the board
     */
    public boolean hasConflict(int row, int column) {
        validatePosition(row, column);

        int value = values[row][column];

        return value != 0 && !isValidMove(row, column, value);
    }

    /**
     * Indicates whether the board is completely filled without conflicts.
     *
     * @return true if every cell is filled and follows the Sudoku rules
     */
    public boolean isSolved() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (values[row][column] == 0 || hasConflict(row, column)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns the allowed numbers for an empty cell.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @return the candidate numbers, or an empty list if the cell is occupied
     * @throws IndexOutOfBoundsException if the position is outside the board
     */
    public ArrayList<Integer> getCandidates(int row, int column) {
        validatePosition(row, column);

        ArrayList<Integer> candidates = new ArrayList<>();

        if (values[row][column] != 0) {
            return candidates;
        }

        for (int value = 1; value <= SIZE; value++) {
            if (isValidMove(row, column, value)) {
                candidates.add(value);
            }
        }

        return candidates;
    }

    /**
     * Returns the number of hints remaining in the current game.
     *
     * @return the remaining hint count
     */
    public int getRemainingHints() {
        return remainingHints;
    }

    /**
     * Counts the empty cells on the board.
     *
     * @return the number of empty cells
     */
    public int getEmptyCellCount() {
        int count = 0;

        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (values[row][column] == 0) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Indicates whether the hint usage limits allow a request.
     *
     * @return true if hints remain and more than one cell is empty
     */
    public boolean canRequestHint() {
        return remainingHints > 0 && getEmptyCellCount() > 1;
    }

    /**
     * Suggests a number that allows the current board to be completed.
     * The original board is not modified.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @return a suggested number, or an empty result if no hint is available
     * @throws IndexOutOfBoundsException if the position is outside the board
     */
    public OptionalInt requestHint(int row, int column) {
        validatePosition(row, column);

        if (!canRequestHint() || values[row][column] != 0) {
            return OptionalInt.empty();
        }

        SudokuBoard copy = new SudokuBoard();

        for (int currentRow = 0; currentRow < SIZE; currentRow++) {
            for (int currentColumn = 0;
                 currentColumn < SIZE;
                 currentColumn++) {

                if (hasConflict(currentRow, currentColumn)) {
                    return OptionalInt.empty();
                }

                copy.values[currentRow][currentColumn] =
                        values[currentRow][currentColumn];
            }
        }

        if (!solveBoard(copy)) {
            return OptionalInt.empty();
        }

        remainingHints--;

        return OptionalInt.of(copy.getValue(row, column));
    }
    /**
     * Checks whether the position belongs to the board.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @throws IndexOutOfBoundsException if the position is outside the board
     */
    private void validatePosition(int row, int column) {
        if (row < 0 || row >= SIZE || column < 0 || column >= SIZE) {
            throw new IndexOutOfBoundsException(
                    "Row and column must be between 0 and " + (SIZE - 1)
            );
        }
    }

    /**
     * Attempts to complete a board using backtracking.
     *
     * @param board the board copy to solve
     * @return true if a complete valid solution is found
     */
    private boolean solveBoard(SudokuBoard board) {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (board.getValue(row, column) == 0) {
                    for (int candidate : board.getCandidates(row, column)) {
                        board.setValue(row, column, candidate);

                        if (solveBoard(board)) {
                            return true;
                        }

                        board.clearCell(row, column);
                    }

                    return false;
                }
            }
        }

        return board.isSolved();
    }
}
