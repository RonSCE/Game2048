package com.codegym.games.game2048;

import com.codegym.engine.cell.*;

import java.util.Arrays;

public class Game2048 extends Game {
    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE]; // Stores the state of the game in a matrix. 0 value = empty cell.

    @Override
    public void initialize() {
        createGame();
        setScreenSize(SIDE, SIDE);
        drawScene();
    }

    private void createGame() {
        createNewNumber();
        createNewNumber();

    }

    private void drawScene() {
        for (int row = 0; row < gameField.length; row++) {
            for (int col = 0; col < gameField[row].length; col++) {
                setCellColoredNumber(row, col, gameField[col][row]);
            }
        }
    }

    private void createNewNumber() {
        int cell, x, y; // initialize variables to find a random empty cell.
        do {
            // attempt to find an empty cell in an (x,y) coordinate
            x = getRandomNumber(SIDE);
            y = getRandomNumber(SIDE);
            // check if the cell is empty.
            cell = gameField[x][y];
        } while (cell != 0);

        int digit = getRandomNumber(10); // random roll to decide if we insert a 2 or a 4.
        gameField[x][y] = ((digit == 9) ? 4 : 2); // insert a '4' at a 10% probability, or a '2' at 90% probability.
    }

    private void setCellColoredNumber(int x, int y, int value) {
        if (value == 0) setCellValueEx(x, y, getColorByValue(value), "");
        else setCellValueEx(x, y, getColorByValue(value), String.valueOf(value));
    }

    private Color getColorByValue(int value) {
        switch (value) {
            case 2:
                return Color.WHITE;
            case 4:
                return Color.BEIGE;
            case 8:
                return Color.ORANGERED;
            case 16:
                return Color.ORANGE;
            case 32:
                return Color.PALEVIOLETRED;
            case 64:
                return Color.RED;
            case 128:
                return Color.LIGHTYELLOW;
            case 256:
                return Color.YELLOW;
            case 512:
                return Color.DARKKHAKI;
            case 1024:
                return Color.PURPLE;
            case 2048:
                return Color.PINK;
            default: // value is '0', blank
                return Color.SNOW;
        }
    }

    private boolean compressRow(int[] row) {
        boolean flag = false; // flag to determine if we made any swaps
        for (int i = 0; i < SIDE - 1; i++) {
            if (row[i] == 0) {
                for (int j = i + 1; j < SIDE; j++) {
                    if (row[j] != 0) {
                        int temp = row[i];
                        row[i] = row[j];
                        row[j] = temp;
                        flag = true;
                        break;
                    }
                }
                if (row[i] == 0) break; // no more numbers left.
            }
        }
        return flag;
    }

    private boolean mergeRow(int[] row) {
        boolean flag = false;
        for (int i = 0; i < SIDE - 1; i++) {
            int cell = row[i];
            if (cell != 0)
                if (row[i + 1] == cell) {
                    row[i] += row[i + 1];
                    row[i + 1] = 0;
                    i++; // advance again to cut down on time.
                    flag = true;
                }
        }
        return flag;
    }

    @Override
    public void onKeyPress(Key key) {
        switch (key) {
            case DOWN:
                moveDown();
                break;
            case LEFT:
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break;
            case UP:
                moveUp();
                break;
        }
    }

    private void moveRight() {

    }

    private void moveLeft() {

    }

    private void moveDown() {

    }

    private void moveUp() {

    }
}//class