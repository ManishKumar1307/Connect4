package com.example.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private HelloController controller;Scene primaryStage;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayground();


        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(stage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        stage.setScene(scene);
        stage.setTitle("Connect Four");
        stage.setResizable(false);
        stage.show();
    }


    private MenuBar createMenu() {

        // File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame());

        MenuItem resetGame = new MenuItem("Reset game");
        resetGame.setOnAction(actionEvent -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(actionEvent -> exitGame());

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem , exitGame);

        // Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Connect4");
        aboutGame.setOnAction(actionEvent -> aboutConnect4());

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> aboutMe());

        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);

        return menuBar;
    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Manish Gangadhar jha");
        alert.setContentText("I Love to play around with code and create games." +
                        "connect 4is one of them. In free time " +
                "I like to spend time with near and dears");
        alert.show();;
    }

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play");
        alert.setContentText("Connect Four is a two-player connection game in which the " +
                "player first choose a color and then take turns dropping colored discs " +
                "from the top into a seven-column, six-row vertically suspended grid. "+
                "The Pieces fall straight down, occupying the next available space within the column. " +
                "The objective of the game is to be the first to form a horizontal, vertical, " +
                "or diagonal line of four of one's own discs. Connect Four is a solved game. "+
                "The first player can always win y playing the right moves");
        alert.show();;
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {

    }

    public static void main(String[] args) {
        launch();
    }
}