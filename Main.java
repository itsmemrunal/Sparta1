package com.internshala.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayGround();

        MenuBar menuBar=createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu(){
        //file menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> controller.resetGame());
        MenuItem resetGame = new MenuItem("Reset Game");
        newGame.setOnAction(event ->controller.resetGame());
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> exitGame());

        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);
         //Help menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Connect4");
        aboutGame.setOnAction(event -> aboutConnect4());

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);


        MenuBar menuBar= new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);
        return menuBar;

    }

    private void aboutMe() {
        Alert alert =new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Mrunal Khade");
        alert.setContentText("I love to play around with code and create games." +
                "Connect4 is one of the them. In free time " +
                "I like to spend with nears and dears");
        alert.show();

    }

    private void aboutConnect4() {
        Alert alert =new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect4 Game");
        alert.setHeaderText("How to Play");
        alert.setContentText("Connect Four is 2 player game in which first choose a color and then " +
                "take value turns drooping colored disc from top into a 7 column,6 row " +
                "vertically suspended grid. The project fall straight down, occupying the " +
                "next available space within the column .The objective of game is to be 1st to" +
                " form a horizontal, vertival or diagonal line of 4 of one's own disc" +
                "Connect4 is a solved game.The 1st player can always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);

    }


    private void reserGame() {
    }

    public static void main(String[] args) {

         launch(args);
    }
}
