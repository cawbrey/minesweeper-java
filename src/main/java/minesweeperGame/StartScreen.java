package minesweeperGame;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class StartScreen extends Application {
    //todo fix all references

    @FXML
    private TextField widthField;

    @FXML
    private TextField heightField;

    @FXML
    private TextField mineField;

    @FXML
    private Button startButton;

    @FXML
    protected void startButtonPress(){
        Scanner widthScnr = new Scanner(widthField.getText());
        Scanner heightScnr = new Scanner(heightField.getText());
        Scanner mineScnr = new Scanner(mineField.getText());

        if(widthScnr.hasNextInt()){
            width = widthScnr.nextInt();
        }

        if(heightScnr.hasNextInt()){
            height = heightScnr.nextInt();
        }

        if(mineScnr.hasNextInt()){
            mines = mineScnr.nextInt();
        }


        launchGame();
    }

    private int width = 10, height = 10, mines = 10;

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Working Directory = " + new File(".").getAbsolutePath());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("start-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Minesweeper");
        stage.setScene(scene);

        stage.show();
    }

    private void launchGame() {
        Stage stage = (Stage) startButton.getScene().getWindow();

        Minesweeper_GUI game = new Minesweeper_GUI();

        game.setValues(width,height,mines);

        game.start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
