package minesweeperGame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.scene.layout.*;

public class Minesweeper_GUI extends Application {
    private final int TILE_EMPTY = 0;
    private final int TILE_COVER = 1;
    private final int TILE_FLAG = 2;
    private final int TILE_QUESTION = 3;
    private final int TILE_BOMB_EXPLODED = 4;
    private final int TILE_BOMB_NO_BOMB = 5;
    private final int TILE_BOMB = 6;
    private final int TILE_EMPTY_QUESTION = 7;
    private final int[] TILE = {0,15,14,13,12,11,10,9,8};


    private int VERTICAL = 10;
    private int HORIZONTAL = 10;
    private int NUM_BOMBS = 10;
    private GridPane tileGridPane;
    private Image[] assets = new Image[16];
    private Game board;
    private ImageView[][] tilesViewers;
    private Scene currentScene;
    private Stage primaryStage;

    //todo add timer and bombs left

    public void setValues(int width, int height, int mines){
        VERTICAL = height;
        HORIZONTAL = width;
        NUM_BOMBS = mines;
    }

    @Override
    public void start(Stage stage){
        primaryStage = stage;
        LoadAssets();
        createBoard();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void createBoard(){
        primaryStage.resizableProperty().setValue(false);
        primaryStage.setTitle("Minesweeper");

        //filling the tiles ImageViews
        tilesViewers = new ImageView[HORIZONTAL][VERTICAL];
        tileGridPane = new GridPane();

        for(int y = 0; y < VERTICAL; y++){
            for(int x = 0; x < HORIZONTAL; x++){
                tilesViewers[x][y] = new ImageView(assets[TILE_COVER]);
            }
        }

        //filling the gridPane with the Image Viewers
        for(int y = 0; y < VERTICAL; y++){
            for(int x = 0; x < HORIZONTAL; x++){
                tileGridPane.add(tilesViewers[x][y], x, y, 1, 1);
            }
        }
        tileGridPane.setOnMouseClicked(this::handleClick);

        currentScene = new Scene(tileGridPane);
        primaryStage.setScene(currentScene);

        primaryStage.show();
    }

    private void updateBoard(){
        //updating tiles
        for(int y = 0; y < VERTICAL; y++){
            for(int x = 0; x < HORIZONTAL; x++){
                tilesViewers[x][y].setImage(assets[board.getBoard()[x][y]]);
            }
        }
    }

    private void handleClick(javafx.scene.input.MouseEvent mouseEvent) {
        int x = (int)(mouseEvent.getX()/16);
        int y = (int)(mouseEvent.getY()/16);

        if(board == null){
            board = new Game(NUM_BOMBS, HORIZONTAL, VERTICAL, x,y);
            updateBoard();
            return;
        }

        System.out.println("x: " + x + " Y: " + y);

        if(mouseEvent.getButton() == MouseButton.PRIMARY){
            board.pressTile(x, y);
        }else if(mouseEvent.getButton() == MouseButton.SECONDARY){
            board.altPressTile(x, y);
        }

        System.out.println("Game Running: " + board.isGameRunning());
        System.out.println("Game Won: " + board.isGameWon());
        System.out.println("Time: " + board.getGameTime());
        //todo need to add timer and bombs left to game screen

        updateBoard();
    }

    private void LoadAssets(){
        for(int i = 0; i < assets.length; i++){
            assets[i] = new Image("file:" + String.format("src/main/resources/assets/%d.bmp", i));
        }
    }
}