package minesweeperGame;

import java.util.Random;

public class Game {
    private final int TILE_BOMB_EXPLODED = 4;
    private final int TILE_BOMB_NO_BOMB = 5;
    private final int TILE_BOMB = 6;
    private final int TILE_EMPTY_QUESTION = 7;
    private final int[] TILE = {0,15,14,13,12,11,10,9,8};

    private final int COVERED_QUESTION = 3;
    private final int COVERED_FLAG = 2;
    private final int COVERED = 1;
    private final int UNCOVERED = 0;


    private int[][][] boardArray; //(0 = board 1 = tile cover), x, y
    public int columns, rows, numBombs;
    private boolean gameRunning, gameWon;

    private double startTime, endTime;

    public Game(int numBombsInput, int columns, int rows, int entryX, int entryY){
        this.columns = columns;
        this.rows = rows;

        gameRunning = true;
        gameWon = false;

        numBombs = Math.min(numBombsInput, (columns * rows) - 1);

        boardArray = new int[2][columns][rows];

        //filling bombs
        int currentNumBombs = 0;
        Random rand = new Random(System.currentTimeMillis());

        while(currentNumBombs < numBombs){
            boardArray[0][rand.nextInt(columns)][rand.nextInt(rows)] = TILE_BOMB;

            //setting entry point
            int TILE_EMPTY = 0;
            boardArray[0][entryX][entryY] = TILE_EMPTY;

            currentNumBombs = 0;
            for(int y = 0; y < rows; y++){
                for(int x = 0; x < columns; x++){
                    if(boardArray[0][x][y] == TILE_BOMB)
                        currentNumBombs++;
                }
            }
        }

        //filling numbers
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){

                if(boardArray[0][x][y] != TILE_BOMB){
                    boardArray[0][x][y] = TILE[bombsSurrounding(x, y)];
                }
            }
        }

        //setting tiles show status
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){
                    boardArray[1][x][y] = COVERED;
            }
        }

        pressTile(entryX, entryY);

        startTime = System.currentTimeMillis();
    }

    private int bombsSurrounding(int x, int y){
        int count = 0;

        //top left

        if(x > 0 && y > 0 && boardArray[0][x-1][y-1] == TILE_BOMB)
            count++;

        //top middle
        if(y > 0 && boardArray[0][x][y-1] == TILE_BOMB)
            count++;

        //top right
        if(x < columns - 1 && y > 0 && boardArray[0][x+1][y-1] == TILE_BOMB)
            count++;

        //right middle
        if(x < columns - 1 && boardArray[0][x+1][y] == TILE_BOMB)
            count++;

        //bottom right
        if(x < columns - 1 && y < rows - 1 && boardArray[0][x+1][y+1] == TILE_BOMB)
            count++;

        //bottom middle
        if(y < rows - 1 && boardArray[0][x][y+1] == TILE_BOMB)
            count++;

        //bottom left
        if(x > 0 && y < rows - 1 && boardArray[0][x-1][y+1] == TILE_BOMB)
            count++;

        //left middle
        if(x > 0 && boardArray[0][x-1][y] == TILE_BOMB)
            count++;

        //return count;
        return count;
    }

    private int flagsSurrounding(int x, int y){
        int count = 0;

        //top left
        if(x > 0 && y > 0 && boardArray[1][x-1][y-1] == COVERED_FLAG)
            count++;

        //top middle
        if(y > 0 && boardArray[1][x][y-1] == COVERED_FLAG)
            count++;

        //top right
        if(x < columns - 1 && y > 0 && boardArray[1][x+1][y-1] == COVERED_FLAG)
            count++;

        //right middle
        if(x < columns - 1 && boardArray[1][x+1][y] == COVERED_FLAG)
            count++;

        //bottom right
        if(x < columns - 1 && y < rows - 1 && boardArray[1][x+1][y+1] == COVERED_FLAG)
            count++;

        //bottom middle
        if(y < rows - 1 && boardArray[1][x][y+1] == COVERED_FLAG)
            count++;

        //bottom left
        if(x > 0 && y < rows - 1 && boardArray[1][x-1][y+1] == COVERED_FLAG)
            count++;

        //left middle
        if(x > 0 && boardArray[1][x-1][y] == COVERED_FLAG)
            count++;

        //return count;
        return count;
    }

    public int[][] getBoard(){
        //return boardArray[0]; //for testing without tile hide

        int[][] outputArray = new int[columns][rows];

        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){
                outputArray[x][y] = boardArray[0][x][y];
            }
        }

        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){

                if(boardArray[1][x][y] != UNCOVERED){
                    outputArray[x][y] = boardArray[1][x][y];
                }
            }
        }

        return outputArray;
    }

    public void pressTile(int inputX, int inputY){

        if(!gameRunning || isFlagged(inputX, inputY)){
            return;
        }

        if(boardArray[0][inputX][inputY] == TILE_BOMB){
            //checking if bomb was pressed
            pressBomb(inputX, inputY);
        }else if(isCovered(inputX, inputY)){
            //checking if covered tile was pressed
            pressCoveredTile(inputX, inputY);
        }else{
            //checking if number tile was pressed
            pressNumberTile(inputX, inputY);
        }

        updateGameState();
    }

    public void altPressTile(int inputX, int inputY){
        if(!isCovered(inputX,inputY) || !gameRunning)
            return;

        if(boardArray[1][inputX][inputY] == COVERED)
            boardArray[1][inputX][inputY] = COVERED_FLAG;

        else if(boardArray[1][inputX][inputY] == COVERED_FLAG)
            boardArray[1][inputX][inputY] = COVERED_QUESTION;

        else if(boardArray[1][inputX][inputY] == COVERED_QUESTION)
            boardArray[1][inputX][inputY] = COVERED;

        updateGameState();
    }

    private void pressNumberTile(int x, int y){
        if(flagsSurrounding(x,y) == bombsSurrounding(x,y)){
            pressCoveredTilesAround(x, y);
        }
    }

    private void pressCoveredTilesAround(int x, int y){
        //top left
        if(x > 0 && y > 0 && boardArray[1][x-1][y-1] == COVERED)
            pressTile(x-1, y-1);

        //top middle
        if(y > 0 && boardArray[1][x][y-1] == COVERED)
            pressTile(x, y-1);

        //top right
        if(x < columns - 1 && y > 0 && boardArray[1][x+1][y-1] == COVERED)
            pressTile(x+1, y-1);

        //right middle
        if(x < columns - 1 && boardArray[1][x+1][y] == COVERED)
            pressTile(x+1, y);

        //bottom right
        if(x < columns - 1 && y < rows - 1 && boardArray[1][x+1][y+1] == COVERED)
            pressTile(x+1, y+1);

        //bottom middle
        if(y < rows - 1 && boardArray[1][x][y+1] == COVERED)
            pressTile(x, y+1);

        //bottom left
        if(x > 0 && y < rows - 1 && boardArray[1][x-1][y+1] == COVERED)
            pressTile(x-1, y+1);

        //left middle
        if(x > 0 && boardArray[1][x-1][y] == COVERED)
            pressTile(x-1, y);
    }

    private void pressCoveredTile(int x, int y){
        if(outOfBounds(x, y) || !isCovered(x, y) ){
            return;
        }

        if(isNumber(x, y)){
            boardArray[1][x][y] = UNCOVERED;
            return;
        }

        if(isEmpty(x, y)){
            boardArray[1][x][y] = UNCOVERED;

            pressCoveredTile(x-1, y);
            pressCoveredTile(x+1, y);
            pressCoveredTile(x, y-1);
            pressCoveredTile(x, y+1);
        }
    }

    private boolean isEmpty(int x, int y){
        return boardArray[0][x][y] == 0;
    }

    private boolean isNumber(int x, int y){
        return boardArray[0][x][y] >= 8;
    }

    private boolean isCovered(int x, int y){
        return boardArray[1][x][y] > 0;
    }

    private boolean isFlagged(int x, int y){
        return boardArray[1][x][y] == COVERED_FLAG;
    }

    private boolean outOfBounds(int x, int y){
        if ( x < 0 )
        {
            return true;
        }

        if ( y < 0 )
        {
            return true;
        }

        if ( x > columns - 1 )
        {
            return true;
        }

        return y > rows - 1;
    }

    private void pressBomb(int inputX, int inputY){
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){
                if(isFlagged(x, y) && boardArray[0][x][y] != TILE_BOMB){
                    boardArray[1][x][y] = TILE_BOMB_NO_BOMB;
                }else if(boardArray[0][x][y] == TILE_BOMB){
                    boardArray[1][x][y] = TILE_BOMB;
                }
            }
        }

        boardArray[1][inputX][inputY] = TILE_BOMB_EXPLODED;

        gameRunning = false;
    }

    public boolean isGameRunning(){
        return gameRunning;
    }

    public boolean isGameWon(){
        return gameWon;
    }

    private void updateGameState(){
        int numCorrectFlags = 0, numCovers = 0;

        for(int y = 0; y < rows; y++){
            for(int x = 0; x < columns; x++){

                if(boardArray[0][x][y] == TILE_BOMB && boardArray[1][x][y] == COVERED_FLAG){
                    numCorrectFlags++;
                }
                if(boardArray[1][x][y] > UNCOVERED){
                    numCovers++;
                }
            }
        }

        if(numCovers == numBombs){
            gameRunning = false;
            gameWon = true;
            endTime = System.currentTimeMillis();
        }else if(numCorrectFlags == numBombs){
            for(int y = 0; y < rows; y++){
                for(int x = 0; x < columns; x++){
                    if(boardArray[1][x][y] == COVERED){
                        pressTile(x,y);
                    }
                }
            }
            endTime = System.currentTimeMillis();
        }
    }

    public double getGameTime(){
        if(isGameWon()){
            //game win timer
            return (endTime - startTime)/1000;
        }else if(isGameRunning() && !isGameWon()){
            //current game time if done
            return (System.currentTimeMillis() - startTime)/1000;
        }else{
            //returns -1 if game lost
            return -1;
        }
    }
}