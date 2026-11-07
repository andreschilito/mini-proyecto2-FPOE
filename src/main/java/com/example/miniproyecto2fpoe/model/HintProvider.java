package com.example.miniproyecto2fpoe.model;

import java.util.OptionalInt;

/**
 * Provides Sudoku hints and information about their availability.
 */
public interface HintProvider {

    /**
     * Returns the number of hints remaining in the current game.
     *
     * @return the remaining hint count
     */
    int getRemainingHints();

    /**
     * Indicates whether the hint usage limits allow a request.
     *
     * @return true if hints remain and more than one cell is empty
     */
    boolean canRequestHint();

    /**
     * Suggests a value that allows the current board to be completed.
     * Board values remain unchanged, and only successful requests consume a hint.
     *
     * @param row the zero-based row index
     * @param column the zero-based column index
     * @return a suggested value, or an empty result if unavailable
     * @throws IndexOutOfBoundsException if the position is outside the board
     */
    OptionalInt requestHint(int row, int column);
}