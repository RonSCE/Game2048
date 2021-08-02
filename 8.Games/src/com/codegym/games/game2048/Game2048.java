package com.codegym.games.game2048;

import com.codegym.engine.cell.*;

/**Main game class, runs the whole game logic and updates the GUI.
 *
 *
 * @version 1.0
 * @see com.codegym.engine.cell.Game
 */
public class Game2048 extends Game {
    // Private fields:
    private int score = 0;  // user's score
    private static final int SIDE = 4;  // board size, it is currently always a square.
    private int[][] gameField = new int[SIDE][SIDE]; // Stores the state of the game in a matrix. 0 value = empty cell.
    private boolean isGameStopped = false;  // flag to determine the continuation of the game.

    /**First function to be called when the game starts.
     * <p>
     *     the function creates the game, sets the playing field size and displays it on the screen.
     * </p>
     *
     */
    @Override
    public void initialize() {
        createGame();
        setScreenSize(SIDE, SIDE);
        drawScene();
    }

    /**This function sets the initial status of the game-
     * <br> it initializes the game field matrix, resets the score and adds the first numbers.
     *
     */
    private void createGame() {
        gameField = new int[SIDE][SIDE]; // reset game field.
        score = 0; // reset score.
        setScore(score); // display reset score.
        createNewNumber();
        createNewNumber();
    }

    /** This function updates the GUI using the setCellColoredNumber method.
     * <p>
     *     The function scans the game field matrix and sets each cell's color appropriately using a helper function.
     * </p>
     *
     */
    private void drawScene() {
        for (int row = 0; row < gameField.length; row++) {
            for (int col = 0; col < gameField[row].length; col++) {
                setCellColoredNumber(row, col, gameField[col][row]);
            }
        }
    }

    /** This function adds a new number to the board.
     * <p>
     *     The function is also responsible for checking if the user has won before creating a new number.
     * </p>
     *<p>
     *     The numbers are added to a randomly selected index in the game field matrix
     *     <br>
     *     Once a free cell has been found there is a 90% chance to add the number '2' or a 10% chance to add '4'.
     *</p>
     *
     *
     */
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

    /**Helper function to set the color of the game field matrix cells.
     * <p>
     *     The function uses a helper function getColorByValue that determines the appropriate color for the cell
     *     based on its value. <br>
     *     It then uses the setCellValueEx method inherited from the Game class to update the GUI display of the cell.
     * </p>
     *
     *
     * @param x - row number of the cell.
     * @param y - column number of the cell.
     * @param value - the value of the cell's contents.
     */
    private void setCellColoredNumber(int x, int y, int value) {
        if (value == 0) setCellValueEx(x, y, getColorByValue(value), "");
        else setCellValueEx(x, y, getColorByValue(value), String.valueOf(value));
    }

    /** Helper function that determines the appropriate color of a cell.
     * <p>
     *     The color uses pre-determined (by the game creator) colors for each possible cell value in the game
     *     to determine the appropriate color for the parameter value.
     * </p>
     *
     *
     * @param value - value of the requested cell.
     * @return an appropriate color for the cell (using Color enum).
     */
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

    /** This function 'trims' a given row of cells so that no spaces ('0' cells) exist between cells with values.
     *
     * @param row - a row of cells from the game field matrix.
     * @return true if a swap was made, false otherwise.
     */
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

    /** This function handles merging of cells in a parameter row.
     * <p>
     *     We merge adjacent cells that have identical values.
     *     <br>Because the cells are always merged left, the matrix must be rotated if a different
     *     direction was chosen.
     * </p>
     *
     * @param row - a row of cells from the game field matrix.
     * @return true if a merge was made, false otherwise.
     */
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

    /** This method handles the logic of moving all the cells to the left.
     * <p>
     *     The function compresses all the rows so that no spaces exist between merge-able adjacent cells.
     *     <br>It then attempts to merge all the merge-able cells, and then compresses again to get rid of the
     *     new blank spaces.
     *     <br>Finally, if a merger or compression happened at any point, we must add a new number to the game field.
     *
     * </p>
     */
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

    /**This method handles the logic of moving all the cells to the right.
     * <p>
     *     we mimic a right-oriented move by turning it to a left-oriented move via rotation.
     *     <br>Thus, we don't have to reimplemented several similar functions, but rather use the left
     *     oriented functions that we made beforehand.
     * </p>
     *
     */
    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    /**This method handles the logic of moving all the cells downwards.
     * <p>
     *     we mimic a downwards-oriented move by turning it to a left-oriented move via rotation.
     *     <br>Thus, we don't have to reimplemented several similar functions, but rather use the left
     *     oriented functions that we made beforehand.
     * </p>
     *
     */
    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    /**This method handles the logic of moving all the cells upwards.
     * <p>
     *     we mimic an upwards-oriented move by turning it to a left-oriented move via rotation.
     *     <br>Thus, we don't have to reimplemented several similar functions, but rather use the left
     *     oriented functions that we made beforehand.
     * </p>
     *
     */
    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    /** Helper function to rotate the game field clockwise.
     *
     */
    private void rotateClockwise() {
        int[][] newArr = new int[SIDE][SIDE]; // create new temporary matrix
        for (int k = 0; k < SIDE; k++) { //use a formula to copy gameField into newArr while rotating clockwise
            for (int l = 0; l < SIDE; l++) {
                newArr[k][l] = gameField[SIDE - l - 1][k];
            }
        }
        gameField = newArr; // update pointer, old matrix should be picked up with the garbage collector.
    }

    /**Helper function that finds the highest cell value in the game field matrix.
     *
     * @return the highest value in the game field.
     */
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

    /** Stops the game and display a winning message!
     *
     */
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.DARKORCHID, "Congratulations, you've won! \nPress space to go again!", Color.BLACK, 30);
    }

    /** Stops the game and display a losing message! :(
     *
     */
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.DARKGRAY, "Sorry, you lost! \nPress space to restart.", Color.ORANGERED, 30);
    }

    /** This method checks if any possible moves exist- compressions/mergers.
     *
     * @return true if the player can make a move, false otherwise.
     */
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