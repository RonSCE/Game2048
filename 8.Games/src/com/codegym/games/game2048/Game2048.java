package com.codegym.games.game2048;

import com.codegym.engine.cell.*;

public class Game2048 extends Game {
    private int score = 0;
    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE]; // Stores the state of the game in a matrix. 0 value = empty cell.
    private boolean isGameStopped = false;

    @Override
    public void initialize() {
        createGame();
        setScreenSize(SIDE, SIDE);
        drawScene();
    }

    private void createGame() {
        gameField = new int[SIDE][SIDE]; // reset game field.
        score = 0; // reset score.
        setScore(score); // display reset score.
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
        if (getMaxTileValue() == 2048)
            win();
        else {
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
                return Color.BLUE;
            case 256:
                return Color.DARKBLUE;
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
                    score += row[i] + row[i+1];
                    setScore(score);
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
        if (isGameStopped) { // if the game is stopped, only the space bar can be pressed
            if (key == Key.SPACE) { // user requested to reset the game.
                isGameStopped = false;
                createGame();
                drawScene();
            }
        } else if (!canUserMove()) { // check if no moves are available
            gameOver(); //end game
        } else {
            switch (key) {
                case DOWN:
                    moveDown();
                    drawScene();
                    break;
                case LEFT:
                    moveLeft();
                    drawScene();
                    break;
                case RIGHT:
                    moveRight();
                    drawScene();
                    break;
                case UP:
                    moveUp();
                    drawScene();
                    break;
            }
        }
    }

    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveLeft() {
        boolean changed = false; // flag to determine if a compression/merger happened at all.
        for (int i = 0; i < SIDE; i++) {
            if (compressRow(gameField[i])) changed = true; //compress the row, update the flag if successful
            if (mergeRow(gameField[i]))
                changed = true; //compress the row // attempt merger of cells, update flag if successful.
            if (compressRow(gameField[i]))
                changed = true; //compress the row, update flag again similarly.
        }
        if (changed) { // if any changes were made, add a new number.
            createNewNumber();
        }

    }

    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void rotateClockwise() {
        int[][] newArr = new int[SIDE][SIDE]; // create new temporary matrix
        for (int k = 0; k < SIDE; k++) { //use a formula to copy gameField into newArr while rotating clockwise
            for (int l = 0; l < SIDE; l++) {
                newArr[k][l] = gameField[SIDE - l - 1][k];
            }
        }
        gameField = newArr; // update pointer, old matrix should be picked up with the garbage collector.
    }

    private int getMaxTileValue() {
        int max = 0;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] > max) {
                    max = gameField[i][j];
                }
            }
        }
        return max;
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.DARKORCHID, "Congratulations, you've won! \nPress space to go again!", Color.BLACK, 30);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.DARKGRAY, "Sorry, you lost! \nPress space to restart.", Color.ORANGERED, 30);
    }

    private boolean canUserMove() {
        for (int i = 0; i < SIDE; i++) { // scan the matrix
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == 0) return true; // check if we have open spaces
                // otherwise, check if we have merges available.
                //horizontal
                if (j < SIDE - 1) // make sure not to go out of bounds
                    if (gameField[i][j] == gameField[i][j + 1]) return true;
                //vertical
                if (i < SIDE - 1) // make sure not to go out of bounds
                    if (gameField[i][j] == gameField[i + 1][j]) return true;
            }
        }
        return false;
    }
}//class